package me.damianciepiela.client;

import me.damianciepiela.Closable;
import me.damianciepiela.Logable;
import me.damianciepiela.LoggerAdapter;

import java.io.IOException;

public final class ClientController extends Controller<ClientModel, ClientView> implements Logable, Closable {

    private final LoggerAdapter logger;

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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            this.logger.error(e);
        }
    }

    private void setClientIdentity() throws IOException, ClassNotFoundException {
        this.view.show("Set your identity: ");
        Identity clientIdentity = this.view.getIdentityFromUser();
        this.model.sendIdentity(clientIdentity.id(), clientIdentity.name(), clientIdentity.surname());
    }

    private void proceedWithTest(int questionsCount) throws IOException, ClassNotFoundException {
        Question question;
        for(int i = 0; i < questionsCount; i++) {
            question = this.model.getQuestion(i);
            String answer = this.view.showQuestionAndGetAnswer(question);
            this.model.sendAnswer(i, answer);
        }
    }

    @Override
    public void close() throws IOException {
        this.model.close();
    }
}
