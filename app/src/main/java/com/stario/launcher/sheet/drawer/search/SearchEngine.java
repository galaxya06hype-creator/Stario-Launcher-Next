/*
 * Copyright (C) 2025 Răzvan Albu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package com.stario.launcher.sheet.drawer.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.stario.launcher.R;
import com.stario.launcher.Stario;
import com.stario.launcher.preferences.Entry;
import com.stario.launcher.sheet.drawer.search.recyclers.adapters.WebAdapter;

public enum SearchEngine {
    GOOGLE("Google", "google.com", "/search?q=", R.drawable.ic_google),
    DUCK_DUCK_GO("DuckDuckGo", "duckduckgo.com", "/?q=", R.drawable.ic_duck),
    BING("Bing", "bing.com", "/search?q=", R.drawable.ic_bing),
    BRAVE("Brave", "search.brave.com", "/search?q=", R.drawable.ic_brave),
    KAGI("Kagi", "kagi.com", "/search?q=", R.drawable.ic_kagi),
    PERPLEXITY("Perplexity AI", "perplexity.ai", "/?s=o&q=", R.drawable.ic_perplexity),
    ECOSIA("Ecosia", "ecosia.org", "/search?q=", R.drawable.ic_ecosia),
    YANDEX("Yandex", "yandex.com", "/search/?text=", R.drawable.ic_yandex),
    YAHOO("Yahoo", "search.yahoo.com", "/search?p=", R.drawable.ic_yahoo),
    CHATGPT("ChatGPT", "chatgpt.com", "/?q=", R.drawable.ic_chatgpt),
    CUSTOM("Custom", "", "/search?q=", R.drawable.ic_search);

    public static final String SEARCH_ENGINE = "com.stario.SEARCH_ENGINE";
    public static final String CUSTOM_SEARCH_URL = "com.stario.CUSTOM_SEARCH_URL";
    public static final String CUSTOM_SEARCH_QUERY = "com.stario.CUSTOM_SEARCH_QUERY";
    private final String label;
    private final String url;
    private final String query;
    private final int drawable;

    SearchEngine(String label, String url, String query, int drawable) {
        this.label = label;
        this.url = url;
        this.query = query;
        this.drawable = drawable;
    }

    public String getUrl() {
        return url;
    }

    public String getCustomUrl(Context context) {
        if (this == CUSTOM) {
            SharedPreferences prefs = context.getSharedPreferences(Entry.SEARCH.toString(), Context.MODE_PRIVATE);
            String custom = prefs.getString(CUSTOM_SEARCH_URL, "");
            if (custom != null && !custom.isEmpty()) return custom;
        }
        return url;
    }

    public String getQuery(String query) {
        return "https://" + url + this.query + query;
    }

    public String getQuery(Context context, String query) {
        if (this == CUSTOM) {
            SharedPreferences prefs = context.getSharedPreferences(Entry.SEARCH.toString(), Context.MODE_PRIVATE);
            String customUrl = prefs.getString(CUSTOM_SEARCH_URL, null);
            String customQuery = prefs.getString(CUSTOM_SEARCH_QUERY, null);
            if (customUrl != null && !customUrl.isEmpty()) {
                String template = customQuery != null && !customQuery.isEmpty() ? customQuery : "/search?q=";
                // Support %s placeholder or simple append
                if (template.contains("%s")) {
                    return "https://" + customUrl + template.replace("%s", query);
                }
                return "https://" + customUrl + template + query;
            }
        }
        return "https://" + url + this.query + query;
    }

    public Drawable getDrawable(Context context) {
        return AppCompatResources.getDrawable(context, drawable);
    }

    public boolean getIsWebOnly() {
        return this == CHATGPT;
    }

    @NonNull
    @Override
    public String toString() {
        return label;
    }

    // Defaults to GOOGLE
    public static SearchEngine getEngine(Stario stario) {
        SearchEngine engine = GOOGLE;

        SharedPreferences preferences = stario.getSharedPreferences(Entry.SEARCH);

        if (preferences.getBoolean(WebAdapter.SEARCH_RESULTS, false)) {
            engine = KAGI;
        } else {
            String engineString = preferences.getString(SEARCH_ENGINE, null);

            if (DUCK_DUCK_GO.url.equals(engineString)) {
                engine = DUCK_DUCK_GO;
            } else if (YANDEX.url.equals(engineString)) {
                engine = YANDEX;
            } else if (BING.url.equals(engineString)) {
                engine = BING;
            } else if (YAHOO.url.equals(engineString)) {
                engine = YAHOO;
            } else if (ECOSIA.url.equals(engineString)) {
                engine = ECOSIA;
            } else if (PERPLEXITY.url.equals(engineString)) {
                engine = PERPLEXITY;
            } else if (KAGI.url.equals(engineString)) {
                engine = KAGI;
            } else if (BRAVE.url.equals(engineString)) {
                engine = BRAVE;
            } else if (CHATGPT.url.equals(engineString)) {
                engine = CHATGPT;
            } else if ("custom".equals(engineString) || CUSTOM.url.equals(engineString)) {
                engine = CUSTOM;
            }
            // Check for custom URL stored as engine string (backward compat for SearXNG, self-hosted)
            if (engine == GOOGLE && engineString != null && engineString.contains(".")) {
                if (!isBuiltinUrl(engineString)) {
                    // treat as custom
                    engine = CUSTOM;
                }
            }
        }

        return engine;
    }

    private static boolean isBuiltinUrl(String url) {
        for (SearchEngine e : values()) {
            if (e != CUSTOM && e.url.equals(url)) return true;
        }
        return false;
    }

    public static void setCustomEngine(Stario stario, String url, String queryTemplate) {
        SharedPreferences prefs = stario.getSharedPreferences(Entry.SEARCH);
        prefs.edit()
                .putString(SEARCH_ENGINE, "custom")
                .putString(CUSTOM_SEARCH_URL, url.replaceFirst("^https?://", "").replaceFirst("/.*$", ""))
                .putString(CUSTOM_SEARCH_QUERY, queryTemplate)
                .apply();
    }

    public static void setEngine(Stario stario, SearchEngine engine) {
        stario.getSharedPreferences(Entry.SEARCH)
                .edit()
                .putString(SEARCH_ENGINE, engine.url)
                .apply();
    }
}
