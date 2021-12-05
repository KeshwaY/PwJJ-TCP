package me.damianciepiela;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public interface ReadQuestions {
    static List<Question> loadQuestionsFromFile(String fileName) throws IOException {
        List<Question> questions = new ArrayList<>();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classloader.getResourceAsStream(fileName);
        if(inputStream == null) return null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while( (line = bufferedReader.readLine()) != null ) {
            List<String> splitByWhiteSpace = Arrays.stream(line.split(" "))
                    .collect(Collectors.toList());
            //TODO: create custom execution
            if (splitByWhiteSpace.size() != 5) return null;
            String description = splitByWhiteSpace.remove(0);
            Optional<String> correctAnswer = splitByWhiteSpace.stream()
                    .filter(answer -> answer.contains("*"))
                    .findFirst()
                    .map(ReadQuestions::getKey);

            if(correctAnswer.isEmpty()) return null;
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
