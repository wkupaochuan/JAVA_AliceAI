/*
 * Copyleft (C) 2005 H閘io Perroni Filho xperroni@bol.com.br ICQ: 2490863 This
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

package bitoflife.chatterbean.aiml;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import bitoflife.chatterbean.util.StringUtils;

public class AIMLHandler extends DefaultHandler {

	/*
	 * Attribute Section
	 */

	private final Set<String> ignored = new HashSet<String>();

	private boolean ignoreWhitespace = true;

	/**
	 * The stack of AIML objects is used to build the Categories as AIML
	 * documents are parsed. The scope is defined as package for testing
	 * purposes.
	 */
	final AIMLStack stack = new AIMLStack();

	final StringBuilder text = new StringBuilder();

	/*
	 * Constructor Section
	 */

	public AIMLHandler(String... ignore) {
		ignored.addAll(Arrays.asList(ignore));
	}

	/*
	 * Method Section
	 */

	private String buildClassName(String tag) {
		return "bitoflife.chatterbean.aiml."
				+ tag.substring(0, 1).toUpperCase()
				+ tag.substring(1).toLowerCase();
	}

	@Override
	public void characters(char[] chars, int start, int length) {
		text.append(chars, start, length);
	}

	@Override
	public void endElement(String namespace, String name, String qname)
			throws SAXException {
		if (ignored.contains(qname)) {
			return;
		}
		pushTextNode();
		ignoreWhitespace = true;
		String className = buildClassName(qname);
		for (List<AIMLElement> children = new LinkedList<AIMLElement>();;) {
			Object tag = stack.pop();
			if (tag == null) {
				throw new SAXException("No matching start tag found for "
						+ qname);
			} else if (!className.equals(tag.getClass().getName())) {
				children.add(0, (AIMLElement) tag);
			} else {
				try {
					if (children.size() > 0) {
						((AIMLElement) tag).appendChildren(children);
					}
					stack.push(tag);
					return;
				} catch (ClassCastException e) {
					throw new RuntimeException(
							"Tag <"
									+ qname
									+ "> used as node, but implementing "
									+ "class does not implement the AIMLElement interface",
							e);
				} catch (Exception e) {
					throw new SAXException(e);
				}
			}
		}
	}

	private void pushTextNode() {
		String pushed = text.toString();
		text.delete(0, text.length());
		if (ignoreWhitespace) {
			pushed = pushed.replaceAll("^[\\s\n]+|[\\s\n]{2,}|\n", " ");
		}
		// 处理中文格式，加空格
		pushed = StringUtils.StringAddSpace(pushed);
		// StringBuffer temp = new StringBuffer();
		// boolean flag = false;
		// for (int i = 0; i < pushed.length(); i++) {
		// java.util.regex.Pattern p = java.util.regex.Pattern
		// .compile("^[\u4E00-\u9FA5|\\*]+$");
		// java.util.regex.Matcher m = p.matcher(String.valueOf(pushed
		// .charAt(i)));
		// if (m.matches()) {
		// if (flag) {
		// temp.append(" ");
		// }
		// temp.append(pushed.charAt(i)).append(" ");
		// flag = false;
		// } else {
		// if ("*".equals(pushed.charAt(i))) {
		// if (flag) {
		// temp.append(" ");
		// }
		// temp.append(pushed.charAt(i)).append(" ");
		// flag = false;
		// } else {
		// temp.append(pushed.charAt(i));
		// flag = true;
		// }
		// }
		// // temp.append(pushed.charAt(i)).append(" ");
		// }
		// pushed = temp.toString();
		// java.lang.System.out.println("pushed=" + pushed);
		if (!"".equals(pushed.trim())) {
			stack.push(new Text(pushed));
		}
	}

	/*
	 * Event Handling Section
	 */

	@Override
	public void startElement(String namespace, String name, String qname,
			Attributes attributes) throws SAXException {
		if (ignored.contains(qname)) {
			return;
		}
		updateIgnoreWhitespace(attributes);
		pushTextNode();
		String className = buildClassName(qname);
		try {
			Class tagClass = Class.forName(className);
			Constructor constructor = tagClass.getConstructor(Attributes.class);
			Object tag = constructor.newInstance(attributes);
			stack.push(tag);
		} catch (Exception e) {
			throw new RuntimeException("Cannot instantiate class " + className,
					e);
		}
	}

	public List<Category> unload() {
		List<Category> result = new LinkedList<Category>();

		Object poped;
		while ((poped = stack.pop()) != null) {
			if (poped instanceof Aiml) {
				result.addAll(((Aiml) poped).children());
			}
		}

		return result;
	}

	private void updateIgnoreWhitespace(Attributes attributes) {
		try {
			ignoreWhitespace = !"preserve".equals(attributes
					.getValue("xml:space"));
		} catch (NullPointerException e) {
		}
	}
}
