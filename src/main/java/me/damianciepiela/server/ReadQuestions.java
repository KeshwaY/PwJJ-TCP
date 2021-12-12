package me.damianciepiela.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

// TODO: case when * is not found
public interface ReadQuestions {
    static List<Question> loadQuestionsFromFile(String fileName) throws IOException, QuestionFormattingException {
        List<Question> questions = new ArrayList<>();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classloader.getResourceAsStream(fileName);
        if(inputStream == null) throw new QuestionFormattingException("File not found");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while( (line = bufferedReader.readLine()) != null ) {
            List<String> splitByWhiteSpace = Arrays.stream(line.split(" "))
                    .collect(Collectors.toList());
            if (splitByWhiteSpace.size() != 5) throw new QuestionFormattingException("Question does not have 4 answers");
            String description = splitByWhiteSpace.remove(0);
            Optional<String> correctAnswer = splitByWhiteSpace.stream()
                    .filter(answer -> answer.contains("*"))
                    .findFirst()
                    .map(ReadQuestions::getKey);

            if(correctAnswer.isEmpty()) throw new QuestionFormattingException("Could not find correct answer");
            Map<String, String> allQuestions = splitByWhiteSpace.stream()
                    .map(answer -> answer.replace("*", ""))
                    .collect(Collectors.toMap(ReadQuestions::getKey, ReadQuestions::getValue));
            questions.add(new Question(description, correctAnswer.get(), allQuestions));
        }
        return questions;
    }

    static String getKey(String answer) {
        return answer.substring(answer.indexOf("[") + 1, answer.indexOf("]"));
    }

    static String getValue(String answer) {
        return answer.substring(answer.indexOf("]") + 1);
    }
}
