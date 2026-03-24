package com.example.plugin.diagrampartitioning;

import java.util.Comparator;

public class AlphaNumComparator<T> implements Comparator<T>
{

	private boolean mCaseInsensitive;

	public AlphaNumComparator()
	{
		mCaseInsensitive = true;
	}

	private int compare(char[] a, int ai, char[] b, int bi)
	{
		while (true) {
			// Handle the case where we run of the end of one or both strings.
			if (ai >= a.length && bi >= b.length) {
				return 0;
			}
			if (ai >= a.length) {
				return -1;
			}
			if (bi >= b.length) {
				return +1;
			}

			char ca = a[ai];
			char cb = b[bi];

			if (ca <= '9' && cb <= '9' && ca >= '0' && cb >= '0') {
				// Current character in each string is a digit, compare the contiguous sequence of digits as integers.

				// First sort out how long the digit sequences are.
				int la = 0;
				int lb = 0;
				while (ai < a.length && ca <= '9' && ca >= '0') {
					la += 1;
					if (++ai < a.length) {
						ca = a[ai];
					}
				}

				while (bi < b.length && cb <= '9' && cb >= '0') {
					lb += 1;
					if (++bi < b.length) {
						cb = b[bi];
					}
				}

				int maxlen = Math.max(la, lb);
				int ina = la - maxlen;
				int inb = lb - maxlen;
				int rina = ai - maxlen;
				int rinb = bi - maxlen;

				// Process each digit in turn, starting with the most significant.
				for (int i = 0; i < maxlen; i++) {
					// If one digit sequence is shorter, we pad it with leading zeroes.
					char cha = (ina++ < 0) ? '0' : a[rina];
					char chb = (inb++ < 0) ? '0' : b[rinb];
					rina++;
					rinb++;

					// The most significant digit with a difference defines the result of the comparison.
					if (cha > chb) {
						return +1;
					}
					if (cha < chb) {
						return -1;
					}
				}
				// Indexes already point to the characters following the numbers.
			}
			else {
				// Character from one or both strings is non-digit, compare the characters themselves.
				if (ca > cb) {
					return +1;
				}
				if (ca < cb) {
					return -1;
				}

				ai++;
				bi++;
			}
		}
	}

	public int compare(T a, T b)
	{

		boolean aNull = a == null;
		boolean bNull = b == null;
		if (aNull && bNull) {
			return 0;
		}
		if (aNull) {
			return -1;
		}
		if (bNull) {
			return 1;
		}

		final String aString = a.toString();
		final String bString = b.toString();
		aNull = aString == null;
		bNull = bString == null;

		if (aNull && bNull) {
			return 0;
		}
		if (aNull) {
			return -1;
		}
		if (bNull) {
			return 1;
		}

		int retVal = 0;
		if (mCaseInsensitive) {
			retVal = compare(aString.toUpperCase().toCharArray(), 0, bString.toUpperCase().toCharArray(), 0);
		}
		else {
			retVal = compare(aString.toCharArray(), 0, bString.toCharArray(), 0);
		}
		return retVal;
	}
}