package org.example.translateapp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.translateapp.models.QueryInfo;
import org.example.translateapp.services.QueryInfoService;
import org.example.translateapp.services.TranslateService;
import org.example.translateapp.translate.TranslateQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

@RestController
public class TranslateController {

    static final int MAX_INPUT_STRING_LENGTH = 32000;

    TranslateService translateService;
    QueryInfoService queryInfoService;

    @Autowired
    public TranslateController(TranslateService translateService, QueryInfoService queryInfoService) {
        this.translateService = translateService;
        this.queryInfoService = queryInfoService;
    }

    @PostMapping("/translate")
    public ResponseEntity<String> translate(@RequestBody TranslateQuery translateQuery,
                                            HttpServletRequest request) throws SQLException {
        if (translateQuery.getSourceText().length() > MAX_INPUT_STRING_LENGTH) {
            return ResponseEntity.badRequest().body("Input string is too long\n");
        }
        Map<String, String> languages = LanguagesController.getLanguages();
        if (!languages.containsKey(translateQuery.getSourceLang()) ||
                !languages.containsKey(translateQuery.getTargetLang())) {
            return ResponseEntity.badRequest().body("Requested language not found\n");
        }

        ResponseEntity<String> response = translateService.translate(translateQuery);

        queryInfoService.saveQueryInfo(QueryInfo.builder()
                                               .userIp(request.getRemoteAddr())
                                               .input(translateQuery.toString())
                                               .output(String.format("http %d, %s",
                                                                     response.getStatusCode().value(),
                                                                     response.getBody()))
                                               .datetime(new Date(System.currentTimeMillis()))
                                               .build());
        return response;
    }

}