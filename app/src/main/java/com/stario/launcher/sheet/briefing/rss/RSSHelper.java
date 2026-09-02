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

package com.stario.launcher.sheet.briefing.rss;

import android.util.Log;

import com.prof18.rssparser.RssParser;
import com.prof18.rssparser.RssParserBuilder;
import com.prof18.rssparser.model.RssChannel;
import com.prof18.rssparser.model.RssItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RSSHelper {
    private static final String TAG = "RSSHelper";
    private static RssParser reader;

    private RSSHelper() {
    }

    public static @NotNull CompletableFuture<@NotNull RssChannel> futureParse(String url) {
        if (reader == null) {
            reader = new RssParserBuilder().build();
        }

        return RSSHelperKt.parseFeed(reader, url);
    }

    public static List<RssItem> parse(String url) {
        if (reader == null) {
            reader = new RssParserBuilder().build();
        }

        try {
            RssChannel channel = RSSHelperKt.parseFeed(reader, url).get();

            return channel.getItems();
        } catch (Exception exception) {
            Log.e(TAG, "parse: ", exception);
        }

        return null;
    }

    // === MOD: Sorting support for #261 ===
    public enum SortOrder { NEWEST_FIRST, OLDEST_FIRST }
    public static List<RssItem> parseSorted(String url, SortOrder order) {
        List<RssItem> items = parse(url);
        if (items == null) return null;
        if (order == SortOrder.OLDEST_FIRST) {
            java.util.Collections.reverse(items);
            // also sort by pubDate if available
            try {
                items.sort((a,b) -> {
                    if (a.getPubDate() == null || b.getPubDate() == null) return 0;
                    return a.getPubDate().compareTo(b.getPubDate());
                });
            } catch (Exception ignored) {}
        } else {
            try {
                items.sort((a,b) -> {
                    if (a.getPubDate() == null || b.getPubDate() == null) return 0;
                    return b.getPubDate().compareTo(a.getPubDate());
                });
            } catch (Exception ignored) {}
        }
        return items;
    }

    // === MOD: Import/Export for #260 OPML ===
    public static String exportToOpml(java.util.List<com.stario.launcher.sheet.briefing.dialog.page.feed.Feed> feeds) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><opml version=\"2.0\"><head><title>Stario Feeds</title></head><body>");
        for (com.stario.launcher.sheet.briefing.dialog.page.feed.Feed f : feeds) {
            sb.append("<outline type=\"rss\" text=\"").append(escapeXml(f.getTitle()))
              .append("\" xmlUrl=\"").append(escapeXml(f.getUrl())).append("\"/>");
        }
        sb.append("</body></opml>");
        return sb.toString();
    }

    public static java.util.List<String> importFromOpml(String opml) {
        java.util.List<String> urls = new java.util.ArrayList<>();
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(opml, "", org.jsoup.parser.Parser.xmlParser());
            for (org.jsoup.nodes.Element el : doc.select("outline[xmlUrl]")) {
                String url = el.attr("xmlUrl");
                if (!url.isEmpty()) urls.add(url);
            }
            // fallback regex
            if (urls.isEmpty()) {
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("xmlUrl=\"([^\"]+)\"").matcher(opml);
                while (m.find()) urls.add(m.group(1));
            }
        } catch (Exception e) {
            Log.e(TAG, "import OPML failed", e);
        }
        return urls;
    }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("\"","&quot;").replace("<","&lt;").replace(">","&gt;");
    }
}
