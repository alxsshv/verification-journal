package com.alxsshv.journal.utils;

import com.ibm.icu.text.Transliterator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransliteratorImpl implements StringTransliterator {
    private static final String CYR_TO_LATIN = "Russian-Latin/BGN";

    @Override
    public String cyrilicToLatin(String string) {
        final Transliterator transliterator = Transliterator.getInstance(CYR_TO_LATIN);
        return transliterator.transliterate(string);
    }
}
