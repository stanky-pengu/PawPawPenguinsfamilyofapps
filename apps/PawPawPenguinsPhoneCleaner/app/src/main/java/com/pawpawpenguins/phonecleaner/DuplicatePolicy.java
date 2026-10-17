package com.pawpawpenguins.phonecleaner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Small pure-Java policy shared by the scanner and host-side tests. */
final class DuplicatePolicy {
    static final class Candidate {
        final String key;
        final String uri;
        final long dateAdded;

        Candidate(String key, String uri, long dateAdded) {
            this.key = key;
            this.uri = uri;
            this.dateAdded = dateAdded;
        }
    }

    private DuplicatePolicy() {}

    /** Returns only redundant copies; the oldest item in each exact-hash group is kept. */
    static List<Candidate> redundantCopies(List<Candidate> candidates) {
        Map<String, List<Candidate>> groups = new LinkedHashMap<>();
        for (Candidate candidate : candidates) {
            groups.computeIfAbsent(candidate.key, ignored -> new ArrayList<>()).add(candidate);
        }
        List<Candidate> redundant = new ArrayList<>();
        for (List<Candidate> group : groups.values()) {
            if (group.size() < 2) continue;
            Collections.sort(group, Comparator
                    .comparingLong((Candidate item) -> item.dateAdded)
                    .thenComparing(item -> item.uri));
            redundant.addAll(group.subList(1, group.size()));
        }
        return redundant;
    }
}
