package software.coley.llzip.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Buffer utils.
 *
 * @author Matt Coley
 */
public class ByteDataUtil {
	public static final int WILDCARD = Integer.MIN_VALUE;

	/**
	 * @param buffer
	 * 		Content to search.
	 * @param pattern
	 * 		Pattern to match.
	 *
	 * @return First index of pattern in content, or {@code -1} for no match.
	 */
	public static long indexOf(ByteData buffer, int[] pattern) {
		return indexOf(buffer, 0, pattern);
	}

	/**
	 * @param data
	 * 		Content to search.
	 * @param offset
	 * 		Offset to begin search at.
	 * @param pattern
	 * 		Pattern to match.
	 *
	 * @return First index of pattern in content, or {@code -1} for no match.
	 */
	public static long indexOf(ByteData data, long offset, int[] pattern) {
		// Remaining data must be as long as pattern
		long limit;
		if (data == null || (limit = data.length()) < pattern.length || offset >= limit)
			return -1;
		// Search from offset going forwards
		for (long i = offset; i < limit; i++)
			if (startsWith(data, i, pattern))
				return i;
		// Not found
		return -1;
	}

	/**
	 * @param data
	 * 		Content to search.
	 * @param offset
	 * 		Offset to begin search at.
	 * @param pattern
	 * 		Pattern to match.
	 *
	 * @return First index of pattern in content, or {@code -1} for no match.
	 */
	public static long indexOfQuad(ByteData data, long offset, int pattern) {
		// Remaining data must be as long as pattern
		long limit;
		if (data == null || (limit = data.length()) < 4 || offset >= limit)
			return -1;
		// Search from offset going forwards
		for (long i = offset; i < limit; i++)
			if (startsWithQuad(data, i, pattern))
				return i;
		// Not found
		return -1;
	}

	/**
	 * @param data
	 * 		Content to search.
	 * @param offset
	 * 		Offset to begin search at.
	 * @param pattern
	 * 		Pattern to match.
	 *
	 * @return First index of pattern in content, or {@code -1} for no match.
	 */
	public static long indexOfWord(ByteData data, long offset, int pattern) {
		// Remaining data must be as long as pattern
		long limit;
		if (data == null || (limit = data.length()) < 2 || offset >= limit)
			return -1;
		// Search from offset going forwards
		for (long i = offset; i < limit; i++)
			if (startsWithWord(data, i, pattern))
				return i;
		// Not found
		return -1;
	}

	/**
	 * @param buffer
	 * 		Content to search.
	 * @param pattern
	 * 		Pattern to match.
	 *
	 * @return Last index of pattern in content, or {@code -1} for no match.
	 */
	public static long lastIndexOf(ByteData buffer, int[] pattern) {
		return lastIndexOf(buffer, (buffer.length() - pattern.length), pattern);
	}

	/**
	 * @param data
	 * 		Content to search.
	 * @param offset
	 * 		Offset to begin search at.
	 * @param pattern
	 * 		Pattern to match.
	 *
	 * @return Last index of pattern in content, or {@code -1} for no match.
	 */
	public static long lastIndexOf(ByteData data, long offset, int[] pattern) {
		// Remaining data must be as long as pattern
		if (data == null || data.length() < pattern.length)
			return -1;
		// Search from offset going backwards
		for (long i = offset; i >= 0; i--)
			if (startsWith(data, i, pattern))
				return i;
		// Not found
		return -1;
	}

	/**
	 * @param data
	 * 		Content to search.
	 * @param offset
	 * 		Offset to begin search at.
	 * @param pattern
	 * 		Pattern to match.
	 *
	 * @return Last index of pattern in content, or {@code -1} for no match.
	 */
	public static long lastIndexOfQuad(ByteData data, long offset, int pattern) {
		// Remaining data must be as long as pattern
		if (data == null || data.length() < 4)
			return -1;
		// Search from offset going backwards
		for (long i = offset; i >= 0; i--)
			if (startsWithQuad(data, i, pattern))
				return i;
		// Not found
		return -1;
	}

	/**
	 * @param data
	 * 		Content to search.
	 * @param offset
	 * 		Offset to begin check at.
	 * @param pattern
	 * 		Pattern to match.
	 *
	 * @return {@code true} when the content of the array at the offset matches the pattern.
	 */
	public static boolean startsWith(ByteData data, long offset, int[] pattern) {
		// Remaining data must be as long as pattern and in the array bounds
		if (data == null || (data.length() - offset) < pattern.length || offset < 0 || offset >= data.length())
			return false;
		// Check for mis-match
		for (int i = 0; i < pattern.length; i++) {
			int p = pattern[i];
			if (p == WILDCARD)
				continue;
			if (data.get(offset + i) != p)
				return false;
		}
		// No mis-match, array starts with pattern
		return true;
	}

	/**
	 * @param data
	 * 		Content to search.
	 * @param offset
	 * 		Offset to begin check at.
	 * @param pattern
	 * 		Pattern to match.
	 *
	 * @return {@code true} when the content of the array at the offset matches the pattern.
	 */
	public static boolean startsWithQuad(ByteData data, long offset, int pattern) {
		// Remaining data must be as long as pattern and in the array bounds
		if (data == null || (data.length() - offset) < 4 || offset < 0 || offset >= data.length())
			return false;
		return readQuad(data, offset) == pattern;
	}

	/**
	 * @param data
	 * 		Content to search.
	 * @param offset
	 * 		Offset to begin check at.
	 * @param pattern
	 * 		Pattern to match.
	 *
	 * @return {@code true} when the content of the array at the offset matches the pattern.
	 */
	public static boolean startsWithWord(ByteData data, long offset, int pattern) {
		// Remaining data must be as long as pattern and in the array bounds
		if (data == null || (data.length() - offset) < 2 || offset < 0 || offset >= data.length())
			return false;
		return readWord(data, offset) == pattern;
	}

	/**
	 * @param data
	 * 		Content to read from.
	 * @param i
	 * 		Index to read word from.
	 *
	 * @return Value of word.
	 */
	public static int readWord(ByteData data, long i) {
		return data.getShort(i) & 0xFFFF;
	}

	/**
	 * @param data
	 * 		Content to read from.
	 * @param i
	 * 		Index to read quad from.
	 *
	 * @return Value of quad.
	 */
	public static int readQuad(ByteData data, long i) {
		return data.getInt(i);
	}

	/**
	 * @param data
	 * 		Content to read from.
	 * @param start
	 * 		Start position of string.
	 * @param len
	 * 		Length of string.
	 *
	 * @return Value of string.
	 */
	public static String readString(ByteData data, long start, int len) {
		if (len == 0)
			return "";
		byte[] bytes = new byte[len];
		data.get(start, bytes, 0, len);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	/**
	 * @param data
	 * 		Content to read from.
	 * @param start
	 * 		Start index.
	 * @param len
	 * 		Length of content to copy.
	 *
	 * @return Copy of range.
	 */
	public static byte[] readArray(ByteData data, int start, int len) {
		byte[] bytes = new byte[len];
		data.get(start, bytes, 0, len);
		return bytes;
	}

	/**
	 * @param data
	 * 		Content to make slice of.
	 * @param start
	 * 		Start index.
	 * @param end
	 * 		Endex index.
	 *
	 * @return Buffer slice.
	 */
	public static ByteBuffer sliceExact(ByteBuffer data, int start, int end) {
		ByteBuffer slice = data.slice();
		slice = ((ByteBuffer) slice.position(start)).slice();
		slice.limit(end - start);
		return slice.order(data.order());
	}

	/**
	 * @param data
	 * 		Content to make slice of.
	 * @param start
	 * 		Start index.
	 * @param len
	 * 		Length of content to make slice of.
	 *
	 * @return Buffer slice.
	 */
	public static ByteData slice(ByteData data, long start, long len) {
		return data.slice(start, start + len);
	}

	/**
	 * @param data
	 * 		Content to get length of.
	 *
	 * @return Buffer length.
	 */
	public static int length(ByteBuffer data) {
		return data.remaining() - data.position();
	}

	/**
	 * @param data
	 * 		Content to get bytes from.
	 *
	 * @return Buffer as byte array.
	 */
	public static byte[] toByteArray(ByteData data) {
		long length = data.length();
		if (length > Integer.MAX_VALUE - 8) {
			throw new IllegalStateException("Data too big!");
		}
		byte[] bytes = new byte[(int) length];
		data.get(0L, bytes, 0, bytes.length);
		return bytes;
	}

	/**
	 * @param data
	 * 		Content to convert to string to.
	 *
	 * @return Buffer as a string.
	 */
	public static String toString(ByteData data) {
		return new String(toByteArray(data), StandardCharsets.UTF_8);
	}

	/**
	 * @param a
	 * 		First buffer.
	 * @param b
	 * 		Second buffer.
	 *
	 * @return {@code true} if buffers are equal.
	 */
	public static boolean equals(ByteData a, ByteData b) {
		return Objects.equals(a, b);
	}
}
