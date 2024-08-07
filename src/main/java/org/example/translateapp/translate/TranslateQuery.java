package org.example.translateapp.translate;

import lombok.Data;

@Data
public class TranslateQuery {

    String sourceLang;

    String targetLang;

    String sourceText;

}
