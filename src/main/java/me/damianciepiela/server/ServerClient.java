package me.damianciepiela.server;

import me.damianciepiela.Connection;
import me.damianciepiela.ConnectionStatus;
import me.damianciepiela.LoggerAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// TODO: probably remove check connection to rely on try catch blocks
public class ServerClient implements Callable<ClientAnswers> {

    private final LoggerAdapter logger;
    private final List<Question> questions;

    private final Socket socket;
    private final DataInputStream inFromClient;
    private final DataOutputStream outToClient;

    private volatile ConnectionStatus connection;
    private int score = 0;

    private final List<ClientAnswer> answers = new ArrayList<>();

    private String id;
    private String name;
    private String surname;

    public interface Observer {
        void update(ConnectionStatus connectionEvent);
    }
    private final Observer observer;

    public ServerClient(Socket socket, LoggerAdapter logger, List<Question> questions, Observer observer) throws IOException {
        this.socket = socket;
        this.logger = logger;
        this.questions = questions;
        this.inFromClient = new DataInputStream(socket.getInputStream());
        this.outToClient = new DataOutputStream(socket.getOutputStream());
        this.connection = ConnectionStatus.ALIVE;
        this.observer = observer;
        this.logger.info("Client created");
    }

    public String getFrom() throws IOException {
        String fromClient = Connection.getFromSource(this.inFromClient);
        this.logger.debug("Got content from Client on " + this.socket.getInetAddress() + ": " + fromClient);
        return fromClient;
    }

    public void sendTo(String text) throws IOException {
        this.logger.debug("Sending content to the Client on " + this.socket.getInetAddress() + ": " + text);
        Connection.sendToSource(this.outToClient, text);
    }

   public void getIdentity() {
       this.logger.info("Getting identity from client...");
       try {
           this.id = getFrom();
           this.name = getFrom();
           this.surname = getFrom();
       } catch (IOException e) {
           this.logger.error(e);
           changeConnectionAndUpdate(ConnectionStatus.LOST);
       }
   }

    public ConnectionStatus getConnection() {
        return connection;
    }

    private void changeConnectionAndUpdate(ConnectionStatus connectionEvent) {
        this.connection = connectionEvent;
        this.logger.info("Client connection changed to: " + connectionEvent);
        this.observer.update(connection);
    }

    public void quit() throws IOException {
        this.inFromClient.close();
        this.outToClient.close();
        this.socket.close();
        changeConnectionAndUpdate(ConnectionStatus.DISCONNECTED);
        this.logger.info("Client connection closed");
        this.logger.debug("Client ID: " + this.id + " score: " + this.score + " / " + this.questions.size());
    }

    @Override
    public ClientAnswers call() {
        try{
            getIdentity();
            sendQuestionCount();
            for(Question question : this.questions) {
                showQuestion(question);
                getAnswerAndCheck(question);
            }
            sendScore();
            quit();
            return new ClientAnswers(this.id, this.name, this.surname, this.score, this.answers);
        } catch (IOException e) {
            this.logger.error(e);
        }
        changeConnectionAndUpdate(ConnectionStatus.LOST);
        return null;
    }

    public void sendScore() throws IOException {
        checkConnection();
        sendTo(this.score + " / " + this.questions.size());
    }

    public void sendQuestionCount() throws IOException {
        checkConnection();
        sendTo(String.valueOf(this.questions.size()));
    }

    public void showQuestion(Question question) throws IOException {
        checkConnection();
        sendTo(question.description());
        Map<String, String> questionMap = IntStream.range(0, question.answers().size()).boxed()
                .collect(Collectors.toMap(ai -> question.answers().get(ai).charRepresentation(), ai -> question.answers().get(ai).description()));
        for(Map.Entry<String, String> entry: questionMap.entrySet()) {
            checkConnection();
            sendTo(entry.getKey());
            checkConnection();
            sendTo(entry.getValue());
        }
    }

    public void getAnswerAndCheck(Question question) throws IOException {
        checkConnection();
        String answer = getFrom();
        if (question.answers().stream().noneMatch(qAnswer -> qAnswer.charRepresentation().equals(answer))) throw new IOException();
        if(question.correctAnswer().equals(answer)) this.score++;
        Answer clientAnswer = question.answers().stream()
                .filter(ans -> ans.charRepresentation().equals(answer))
                .findAny()
                .orElseThrow(IOException::new);
        this.answers.add(new ClientAnswer(question.ID(), clientAnswer.ID()));
    }

    private void checkConnection() throws IOException {
        boolean connectionStatus = Connection.checkIfSourceIsActive(this.outToClient, this.inFromClient);
        //this.logger.debug("Client on " + this.socket.getInetAddress() + " connection status: " + this.connected);
        if(!connectionStatus) changeConnectionAndUpdate(ConnectionStatus.LOST);
    }
}
