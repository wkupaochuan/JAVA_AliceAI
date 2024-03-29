/*
 * Copyleft (C) 2005 H�lio Perroni Filho xperroni@yahoo.com ICQ: 2490863 This
 * file is part of ChatterBean. ChatterBean is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. ChatterBean is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with ChatterBean (look at the
 * Documents/ directory); if not, either write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA, or visit
 * (http://www.gnu.org/licenses/gpl.txt).
 */

package bitoflife.chatterbean.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * �жϾ���
 */
public class Sentence {

	/*
	 * Attributes
	 */

	public static final Sentence ASTERISK = new Sentence(" * ", new Integer[] {
			0, 2 }, " * ");

	private Integer[] mappings; // The index mappings of normalized elements to

	// original elements.

	private String normalized;

	private String original;

	private String[] splitted; // The normalized entry, splitted in an array of

	public List<String> starValue = new ArrayList<String>();

	/**
	 * Constructs a Sentence out of a non-normalized input string.
	 */
	public Sentence(String original) {
		this(original, null, null);
	}

	// words.

	public Sentence(String original, Integer[] mappings, String normalized) {
		setOriginal(original);
		setMappings(mappings);
		setNormalized(normalized);
	}

	/*
	 * Constructors
	 */

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Sentence)) {
			return false;
		}
		Sentence compared = (Sentence) obj;
		return original.equals(compared.original)
				&& Arrays.equals(mappings, compared.mappings)
				&& normalized.equals(compared.normalized);
	}

	public Integer[] getMappings() {
		return mappings;
	}

	/*
	 * Methods
	 */

	/**
	 * Gets the Sentence in its normalized form.
	 */
	public String getNormalized() {
		return normalized;
	}

	/**
	 * Gets the Sentence, in its original, unformatted form.
	 */
	public String getOriginal() {
		return original;
	}

	public List<String> getStarValue() {
		return starValue;
	}

	/**
	 * Gets the number of individual words contained by the Sentence.
	 */
	public int length() {
		return splitted.length;
	}

	/**
	 * Returns the normalized as an array of String words.
	 */
	public String[] normalized() {
		return splitted;
	}

	/**
	 * Gets the (index)th word of the Sentence, in its normalized form.
	 */
	public String normalized(int index) {
		return splitted[index];
	}

	public String original(int beginIndex, int endIndex) {
		if (beginIndex < 0) {
			throw new ArrayIndexOutOfBoundsException(beginIndex);
		}

		while (beginIndex >= 0 && mappings[beginIndex] == null) {
			beginIndex--;
		}

		int n = mappings.length;
		while (endIndex < n && mappings[endIndex] == null) {
			endIndex++;
		}

		if (endIndex >= n) {
			endIndex = n - 1;
		}

		String value = original.substring(mappings[beginIndex],
				mappings[endIndex] + 1);

		value = value.replaceAll(
				"^[^A-Za-z0-9\u4E00-\u9FA5]+|[^A-Za-z0-9\u4E00-\u9FA5]+$", " ");
		return value;
	}

	/*
	 * Properties
	 */

	public void setMappings(Integer[] mappings) {
		this.mappings = mappings;
	}

	public void setNormalized(String normalized) {
		this.normalized = normalized;
		if (normalized != null) {
			splitted = normalized.trim().split(" ");
		}
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public void setStarValue(List<String> starValue) {
		this.starValue = starValue;
	}

	/**
	 * Returns a string representation of the Sentence. This is useful for
	 * printing the state of Sentence objects during tests.
	 * 
	 * @return A string formed of three bracket-separated sections: the original
	 *         sentence string, the normalized-to-original word mapping array,
	 *         and the normalized string.
	 */
	@Override
	public String toString() {
		return "[" + original + "]" + Arrays.toString(mappings) + "["
				+ normalized + "]";
	}

	/**
	 * Returns a trimmed version of the original Sentence string.
	 * 
	 * @return A trimmed version of the original Sentence string.
	 */
	public String trimOriginal() {
		return original.trim();
	}
}
