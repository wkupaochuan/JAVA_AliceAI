/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
 */

package bitoflife.chatterbean.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bitoflife.chatterbean.aiml.Category;

public class Request {
	/*
	 * Attributes
	 */

	private Category category;
	private String lexicon;
	private String original;
	private Sentence[] sentences;
	private List<String> starValue = new ArrayList<String>();

	public Request() {
	}

	public Request(String original, Sentence... sentences) {
		this.original = original;
		this.sentences = sentences;
	}

	/*
	 * Constructor
	 */

	public Request(String original, String lexicon) {
		this.original = original;
		this.lexicon = lexicon;
	}

	public String answer() {
		return original.trim().replaceAll(" ", "");
	}

	public boolean empty() {
		return (sentences == null || sentences.length == 0);
	}

	/*
	 * Methods
	 */

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Request)) {
			return false;
		}

		Request compared = (Request) obj;
		return original.equals(compared.original)
				&& Arrays.equals(sentences, compared.sentences);
	}

	public Category getCategory() {
		return category;
	}

	public String getLexicon() {
		return lexicon;
	}

	public String getOriginal() {
		return original;
	}

	public Sentence[] getSentences() {
		return sentences;
	}

	/*
	 * Properties
	 */

	public Sentence getSentences(int index) {
		return sentences[index];
	}

	public List<String> getStarValue() {
		return starValue;
	}

	public Sentence lastSentence(int index) {
		return sentences[sentences.length - (1 + index)];
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void setLexicon(String lexicon) {
		this.lexicon = lexicon;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public void setSentences(Sentence[] sentences) {
		this.sentences = sentences;
	}

	public void setStarValue(List<String> starValue) {
		this.starValue = starValue;
	}

	@Override
	public String toString() {
		return original;
	}

	public String trimOriginal() {
		return original.trim();
	}
}