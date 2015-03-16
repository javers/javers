/**
 * The idea is based on calculating the Levenshtein edit distance for two strings. That is
 * answering the question what changes does it take to go from e.g. "abcd" to "ecf"?
 *
 * Since a list of characters (i.e. strings) is equal to a list of objects up to isomorphism
 * we can use the same algorithm as for finding the Levenshtein edit distance for strings.
 *
 * The algorithm is based on computing the shortest path in a DAG. It takes both O(nm) space
 * and time. Further work should improve it to take O(n) space and O(nm) time (n and m being
 * the length of both compared lists).
 *
 */
package org.javers.core.diff.appenders.levenshtein;
