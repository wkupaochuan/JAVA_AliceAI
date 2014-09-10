/*
Copyleft (C) 2005 H閘io Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
 */

package bitoflife.chatterbean;

import static bitoflife.chatterbean.text.Sentence.ASTERISK;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bitoflife.chatterbean.text.Sentence;

/**
 * Contains information about a match operation, which is needed by the classes
 * of the <code>bitoflife.chatterbean.aiml</code> to produce a proper
 * response.
 */
public class Match implements Serializable {
	/*
	 * Inner Classes
	 */

	public enum Section {
		ENUM, PATTERN, THAT, TOPIC;
	}

	/*
	 * Attributes
	 */

	/**
	 * Version class identifier for the serialization engine. Matches the number
	 * of the last revision where the class was created / modified.
	 */
	private static final long serialVersionUID = 8L;

	private AliceBot callback;

	private Sentence input;

	private String[] matchPath;

	// private final Map<Section, List<String>> sections = new HashMap<Section,
	// List<String>>();
	private final Map<Section, Map<String, List<String>>> sections = new HashMap<Section, Map<String, List<String>>>();

	private Sentence that;

	private Sentence topic;

	private String userId;

	{
		sections.put(Section.PATTERN, new HashMap<String, List<String>>()); // Pattern
		// wildcards
		sections.put(Section.THAT, new HashMap<String, List<String>>()); // That
		// wildcards
		sections.put(Section.TOPIC, new HashMap<String, List<String>>()); // Topic
		// wildcards
		sections.put(Section.ENUM, new HashMap<String, List<String>>()); // enum
		// sections.put(Section.PATTERN, new ArrayList<String>(2)); // Pattern
		// // wildcards
		// sections.put(Section.THAT, new ArrayList<String>(2)); // That
		// // wildcards
		// sections.put(Section.TOPIC, new ArrayList<String>(2)); // Topic
		// // wildcards
		// sections.put(Section.ENUM, new ArrayList<String>(2)); // enum
	}

	/*
	 * Constructor
	 */

	public Match() {
	}

	public Match(AliceBot callback, Sentence input, Sentence that,
			Sentence topic, String userId) {
		this.callback = callback;
		this.input = input;
		this.that = that;
		this.topic = topic;
		this.userId = userId;
		setUpMatchPath(input.normalized(), that.normalized(), topic
				.normalized());
	}

	public Match(Sentence input, String userId) {
		this(null, input, ASTERISK, ASTERISK, userId);
	}

	/*
	 * Methods
	 */

	public void appendWildcard(int beginIndex, int endIndex, String userId) {
		int inputLength = input.length();
		if (beginIndex <= inputLength) {
			if (sections.get(Section.PATTERN).get(userId) == null) {
				sections.get(Section.PATTERN).put(userId,
						new ArrayList<String>(2));
			}
			appendWildcard(sections.get(Section.PATTERN).get(userId), input,
					beginIndex, endIndex);
			return;
		}

		beginIndex = beginIndex - (inputLength + 1);
		endIndex = endIndex - (inputLength + 1);

		int thatLength = that.length();
		if (beginIndex <= thatLength) {
			if (sections.get(Section.THAT).get(userId) == null) {
				sections.get(Section.THAT)
						.put(userId, new ArrayList<String>(2));
			}
			appendWildcard(sections.get(Section.THAT).get(userId), that,
					beginIndex, endIndex);
			return;
		}

		beginIndex = beginIndex - (thatLength + 1);
		endIndex = endIndex - (thatLength + 1);

		int topicLength = topic.length();
		if (beginIndex < topicLength) {
			if (sections.get(Section.TOPIC).get(userId) == null) {
				sections.get(Section.TOPIC).put(userId,
						new ArrayList<String>(2));
			}
			appendWildcard(sections.get(Section.TOPIC).get(userId), topic,
					beginIndex, endIndex);
		}
	}

	private void appendWildcard(List<String> section, Sentence source,
			int beginIndex, int endIndex) {
		if (beginIndex == endIndex) {
			section.add(0, "");
		} else {
			try {
				section.add(0, source.original(beginIndex, endIndex));
			} catch (Exception e) {
				// throw new RuntimeException("Source: {\"" +
				// source.getOriginal() + "\", \"" + source.getNormalized() +
				// "\"}\n" +
				// "Begin Index: " + beginIndex + "\n" +
				// "End Index: " + endIndex, e);
			}
		}
	}

	/**
	 * 添加自定义分词
	 * 
	 * @param starValue
	 *            自定義分詞傳遞
	 */
	public void appendWildcard(List<String> starValue, String userId) {
		List<String> list = sections.get(Section.ENUM).get(userId);
		if (list == null) {
			list = new ArrayList<String>(2);
			list.addAll(starValue);
			sections.get(Section.ENUM).put(userId, list);
		} else {
			sections.get(Section.ENUM).get(userId).addAll(starValue);
		}
	}

	public AliceBot getCallback() {
		return callback;
	}

	public String[] getMatchPath() {
		return matchPath;
	}

	/*
	 * Properties
	 */

	public String getMatchPath(int index) {
		return matchPath[index];
	}

	public int getMatchPathLength() {
		return matchPath.length;
	}

	public String getUserId() {
		return userId;
	}

	public void setCallback(AliceBot callback) {
		this.callback = callback;
	}

	private void setUpMatchPath(String[] pattern, String[] that, String[] topic) {
		int m = pattern.length, n = that.length, o = topic.length;
		matchPath = new String[m + 1 + n + 1 + o];
		matchPath[m] = "<THAT>";
		matchPath[m + 1 + n] = "<TOPIC>";

		System.arraycopy(pattern, 0, matchPath, 0, m);
		System.arraycopy(that, 0, matchPath, m + 1, n);
		System.arraycopy(topic, 0, matchPath, m + 1 + n + 1, o);
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Gets the contents for the (index)th wildcard in the matched section.
	 */
	public String wildcard(Section section, int index, String userId) {
		List<String> wildcards = sections.get(section).get(userId);
		// fixed by lcl
		if (wildcards.size() == 0) {
			return "";
		}
		int i = index - 1;
		if (i < wildcards.size() && i > -1) {
			return wildcards.get(i);
		} else {
			return "";
		}
	}
}
