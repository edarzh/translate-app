package org.example.translateapp.services;

import org.example.translateapp.translate.TranslateQuery;
import org.example.translateapp.translate.TranslateRecursiveTask;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Service
public class TranslateService {

    public ResponseEntity<String> translate(TranslateQuery translateQuery) {
        final int MAX_THREADS = 10;
        ForkJoinPool forkJoinPool = new ForkJoinPool(Math.min(Runtime.getRuntime().availableProcessors(), MAX_THREADS),
                                                     ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                                                     null,
                                                     false,
                                                     0,
                                                     MAX_THREADS,
                                                     1,
                                                     null,
                                                     60,
                                                     TimeUnit.SECONDS);
        TranslateRecursiveTask task = new TranslateRecursiveTask(translateQuery.getSourceLang(),
                                                                 translateQuery.getTargetLang(),
                                                                 List.of(translateQuery.getSourceText().split(" ")));
        try {
            String targetText = forkJoinPool.invoke(task);
            return ResponseEntity.ok(targetText + "\n");
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString() + "\n");
        }
    }

}
