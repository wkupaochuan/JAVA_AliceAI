package bitoflife.chatterbean.aiml;

import org.xml.sax.Attributes;

import bitoflife.chatterbean.Match;

public class Userid extends TemplateElement {
	/**
	 * 初始化执行才构造函数
	 * 
	 * @param attributes
	 */
	public Userid(Attributes attributes) {
		// try {
		// GuideBot mother = new GuideBot();
		// mother.setUp();
		// botInstance = mother.newInstance();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public String process(Match match) {
		try {
			return match.getUserId();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
