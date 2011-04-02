package com.net.finditnow;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestions extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.net.finditnow.SearchSuggestions";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestions() {
        setupSuggestions(AUTHORITY, MODE);
    }
}