package bitoflife.chatterbean.aiml;

import org.xml.sax.Attributes;

import bitoflife.chatterbean.Match;

public class Cs extends TemplateElement {
	public Cs() {
	}

	public Cs(Attributes attributes) {
	}

	@Override
	public int hashCode() {
		return 131072;
	}

	@Override
	public String process(Match match) {
		java.lang.System.out.println("===========");
		return "===";
	}
}
