package me.damianciepiela.client;

import me.damianciepiela.Closable;
import me.damianciepiela.ConnectionStatus;
import me.damianciepiela.Logable;
import me.damianciepiela.LoggerAdapter;

import java.io.IOException;
import java.util.concurrent.*;

public final class ClientController extends Controller<ClientModel, ClientView> implements Logable, Closable {

    private final LoggerAdapter logger;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public ClientController(ClientModel model, ClientView view, LoggerAdapter logger) {
        this.view = view;
        this.model = model;
        this.logger = logger;
    }

    public void start() {
        try{
            setClientIdentity();
            int questionsCount = this.model.getQuestionsCount();
            proceedWithTest(questionsCount);
            this.view.show(this.model.getFinalScore());
            close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            this.logger.error(e);
        }
    }

    private void setClientIdentity() throws IOException {
        this.view.show("Set your identity: ");
        Identity clientIdentity = this.view.getIdentityFromUser();
        this.model.sendIdentity(clientIdentity.id(), clientIdentity.name(), clientIdentity.surname());
    }

    private void proceedWithTest(int questionsCount) throws IOException {
        Question question;
        for(int i = 0; i < questionsCount; i++) {
            System.out.println("test");
            question = this.model.getQuestion();
            ServerTimer timer = new ServerTimer(this.model, (String serverFlag) -> {
               if(serverFlag.equals(ConnectionStatus.FORCE_NEXT_QUESTION.name())) this.view.cancelWaitingForUserInput();
            });
            this.executorService.submit(timer);
            String userInput = this.view.showQuestionAndGetAnswer(question);
            if(userInput != null) this.model.sendToServer(userInput);
        }
    }

    @Override
    public void close() throws IOException {
        this.model.close();
    }
}
