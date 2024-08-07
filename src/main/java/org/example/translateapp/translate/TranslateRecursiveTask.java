package org.example.translateapp.translate;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class TranslateRecursiveTask extends RecursiveTask<String> {

    private final List<String> words;
    private final String sourceLang;
    private final String targetLang;

    private static final int THRESHOLD = 1;

    private static final String SRC_LANG_URI_VAR_NAME = "sourceLanguage";
    private static final String SRC_TEXT_URI_VAR_NAME = "sourceText";
    private static final String TARGET_LANG_URI_VAR_NAME = "targetLanguage";
    private static final String GET_TRANSLATION_URI_TEMPLATE = String.format(
            "https://ftapi.pythonanywhere.com/translate?sl={%s}&dl={%s}&text={%s}",
            SRC_LANG_URI_VAR_NAME,
            TARGET_LANG_URI_VAR_NAME,
            SRC_TEXT_URI_VAR_NAME);

    private String translate(String word) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put(SRC_LANG_URI_VAR_NAME, sourceLang);
        uriVariables.put(TARGET_LANG_URI_VAR_NAME, targetLang);
        uriVariables.put(SRC_TEXT_URI_VAR_NAME, word);

        RestTemplate restTemplate = new RestTemplate();

        Translation translation = restTemplate.getForObject(GET_TRANSLATION_URI_TEMPLATE,
                                                            Translation.class,
                                                            uriVariables);
        if (translation == null) {
            throw new RuntimeException("null response from translation service");
        }
        return translation.targetText;
    }

    public TranslateRecursiveTask(String sourceLang, String targetLang, List<String> words) {
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.words = words;
    }

    @Override
    protected String compute() {
        if (words.size() > THRESHOLD) {
            return ForkJoinTask.invokeAll(createSubtasks())
                    .stream()
                    .map(ForkJoinTask::join)
                    .collect(Collectors.joining(" "));
        } else {
            return translate(words.get(0));
        }
    }

    private Collection<TranslateRecursiveTask> createSubtasks() {
        List<TranslateRecursiveTask> tasks = new ArrayList<>();
        tasks.add(new TranslateRecursiveTask(sourceLang, targetLang, words.subList(0, words.size() / 2)));
        tasks.add(new TranslateRecursiveTask(sourceLang, targetLang, words.subList(words.size() / 2, words.size())));
        return tasks;
    }


    private static class Translation {
        @JsonProperty("destination-text")
        String targetText;
    }
}
