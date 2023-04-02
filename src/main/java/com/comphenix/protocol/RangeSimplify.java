package com.comphenix.protocol;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;

import java.util.ArrayList;
import java.util.List;


public class RangeSimplify {
    RangeSimplify() {}

    /**
     * Simplify a list of ranges by assuming a maximum value.
     * @param ranges - the list of ranges to simplify.
     * @param maximum - the maximum value (minimum value is always 0).
     * @return A simplified list of ranges.
     */
    static List<Range<Integer>> simplify(List<Range<Integer>> ranges, int maximum) {
        List<Range<Integer>> result = new ArrayList<>();
        boolean[] set = new boolean[maximum + 1];
        int start = -1;

        // Set every ID
        for (Range<Integer> range : ranges) {
            for (int id : ContiguousSet.create(range, DiscreteDomain.integers())) {
                set[id] = true;
            }
        }

        // Generate ranges from this set
        for (int i = 0; i <= set.length; i++) {
            if (i < set.length && set[i]) {
                if (start < 0) {
                    start = i;
                }
            } else {
                if (start >= 0) {
                    result.add(Range.closed(start, i - 1));
                    start = -1;
                }
            }
        }

        return result;
    }
}
