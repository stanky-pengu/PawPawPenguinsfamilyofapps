package com.pawpawpenguins.phonecleaner;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class DuplicatePolicyTest {
    @Test public void keepsOldestAndOnlyRemovesSameHashCopies() {
        List<DuplicatePolicy.Candidate> input = Arrays.asList(
                new DuplicatePolicy.Candidate("same-hash", "content://newer", 30),
                new DuplicatePolicy.Candidate("different-hash", "content://unique", 5),
                new DuplicatePolicy.Candidate("same-hash", "content://oldest", 10),
                new DuplicatePolicy.Candidate("same-hash", "content://middle", 20));
        List<DuplicatePolicy.Candidate> redundant = DuplicatePolicy.redundantCopies(input);
        assertEquals(2, redundant.size());
        assertEquals("content://middle", redundant.get(0).uri);
        assertEquals("content://newer", redundant.get(1).uri);
    }

    @Test public void leavesEveryUniqueFileAlone() {
        List<DuplicatePolicy.Candidate> input = Arrays.asList(
                new DuplicatePolicy.Candidate("one", "a", 1),
                new DuplicatePolicy.Candidate("two", "b", 2));
        assertEquals(0, DuplicatePolicy.redundantCopies(input).size());
    }
}
