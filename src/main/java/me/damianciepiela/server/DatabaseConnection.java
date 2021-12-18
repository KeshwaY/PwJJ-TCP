package me.damianciepiela.server;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;


// TODO: make two separate classes
public class DatabaseConnection {

    private final Connection connection;

    private record Answers(List<Answer> answers, int correctAnswer) {
        @Override
        public List<Answer> answers() {
            return answers;
        }

        @Override
        public int correctAnswer() {
            return correctAnswer;
        }

    }

    public DatabaseConnection(String host, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(host, user, password);
        createDatabase();
        createTables();
    }

    private void createDatabase() throws SQLException {
       execute("CREATE DATABASE IF NOT EXISTS java_task");
       execute("USE java_task");
    }

    private void createTables() throws SQLException {
        createQuestionTable();
        createAnswerTable();
        connectQuestionAndAnswer();

        createStudentTable();
        createStudentAnswerTable();
        createStudentScoreTable();
    }

    private void createQuestionTable() throws SQLException {
        execute("CREATE TABLE IF NOT EXISTS question ( ID INTEGER AUTO_INCREMENT PRIMARY KEY, description VARCHAR(255) NOT NULL )");
    }

    private void createAnswerTable() throws SQLException {
        execute("CREATE TABLE IF NOT EXISTS answer ( ID INTEGER AUTO_INCREMENT PRIMARY KEY, description VARCHAR(255) NOT NULL )");
    }

    private void connectQuestionAndAnswer() throws SQLException{
        execute("CREATE TABLE IF NOT EXISTS question_answer ( questionID INTEGER NOT NULL, answerID INTEGER NOT NULL, isCorrect BOOLEAN, PRIMARY KEY (questionID, answerID), FOREIGN KEY(questionID) REFERENCES question(ID), FOREIGN KEY(answerID) REFERENCES answer(ID) )");
    }

    private void createStudentTable() throws SQLException {
        execute("CREATE TABLE IF NOT EXISTS student ( ID VARCHAR(8) PRIMARY KEY, name VARCHAR(255) NOT NULL, surname VARCHAR(255) NOT NULL )");
    }

    private void createStudentAnswerTable() throws SQLException {
        execute("CREATE TABLE IF NOT EXISTS student_answer (id INTEGER AUTO_INCREMENT PRIMARY KEY, studentID VARCHAR(8) NOT NULL, questionID INTEGER NOT NULL, answerID INTEGER, FOREIGN KEY (studentID) REFERENCES student(ID), FOREIGN KEY (questionID) REFERENCES question(ID), FOREIGN KEY (answerID) REFERENCES answer(ID) )");
    }

    private void createStudentScoreTable() throws SQLException {
        execute("CREATE TABLE IF NOT EXISTS student_score (studentID VARCHAR(8) NOT NULL, score INTEGER NOT NULL, PRIMARY KEY (studentID), FOREIGN KEY (studentID) REFERENCES student(ID) )");
    }

    private boolean checkForValue(String sql) throws SQLException{
        Statement statement = this.connection.createStatement();
        return statement.execute(sql);
    }

    private boolean execute(String sql) throws SQLException {
        Statement statement = this.connection.createStatement();
        return statement.execute(sql);
    }

    private ResultSet executeQuery(String sql) throws SQLException {
        Statement statement = this.connection.createStatement();
        return statement.executeQuery(sql);
    }

    public boolean checkIfStudentExists(String studentId) throws SQLException {
        return checkForValue("SELECT * FROM student WHERE ID = " + studentId);
    }

    public List<Question> getQuestions() throws SQLException {
        List<Question> questions = new LinkedList<>();
        int questionCount = getQuestionCount();

        List<String> answerToMap = List.of("a","b","c","d");

        for(int i = 0; i < questionCount; i++) {
            String description = getDescription(i + 1);
            Answers answers = getAnswers(i + 1);
            String correctAnswer = answerToMap.get(answers.correctAnswer());
            questions.add(new Question(String.valueOf(i + 1), description, correctAnswer, answers.answers()));
        }

        return questions;
    }

    public int getQuestionCount() throws SQLException {
        ResultSet questionCount = executeQuery("SELECT COUNT(*) AS total FROM question");
        if(questionCount.next()) {
            return questionCount.getInt("total");
        } else {
            throw new SQLException("No results");
        }
    }

    private String getDescription(int questionId) throws SQLException {
        ResultSet description = executeQuery("SELECT description FROM question WHERE ID = " + questionId);
        if(!description.next()) throw new SQLException("No results");
        return  description.getString("description");
    }

    private Answers getAnswers(int questionId) throws SQLException {
        List<String> answersIDList = new LinkedList<>();
        List<String> answersList = new LinkedList<>();
        List<String> answerToMap = List.of("a","b","c","d");

        int positionOfCorrectAnswer = 0;
        int count = 0;
        ResultSet answers = executeQuery("SELECT answerID, description, isCorrect FROM answer as a JOIN question_answer qa on a.ID = qa.answerID WHERE qa.questionID = " + questionId);
        if(!answers.next()) throw new SQLException("Missing results");
        do {
            answersIDList.add(String.valueOf(answers.getInt("answerID")));
            answersList.add(answers.getString("description"));
            if (answers.getBoolean("isCorrect")) positionOfCorrectAnswer = count;
            count++;
        } while(answers.next());

        List<Answer> answersToReturn = new LinkedList<>();
        //TODO: stream
        for(int i = 0; i < answerToMap.size(); i++) {
            answersToReturn.add(new Answer(answersIDList.get(i), answerToMap.get(i), answersList.get(i)));
        }

        return new Answers(answersToReturn, positionOfCorrectAnswer);
    }

    public boolean addStudent(String id, String name, String surname) throws SQLException {
        return execute(String.format("INSERT INTO student(ID, name, surname) VALUES ('%s', '%s', '%s')", id, name, surname));
    }

    public boolean saveStudentAnswer(String studentId, String questionId, String answerId) throws SQLException {
        return execute(String.format("INSERT INTO student_answer(studentID, questionID, answerID) VALUES ('%s', %s, %s)", studentId, questionId, answerId));
    }

    public boolean saveStudentScore(String studentID, String score) throws SQLException {
        return execute(String.format("INSERT INTO student_score(studentID, score) VALUES (%s, '%s')", studentID, score));
    }

    public void close() throws SQLException {
        this.connection.close();
    }

}
