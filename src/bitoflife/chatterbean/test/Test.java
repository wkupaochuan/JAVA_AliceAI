package bitoflife.chatterbean.test;

import bitoflife.chatterbean.text.Response;
import bitoflife.chatterbean.util.StringUtils;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// �ڶ����������û�id
		QaBot.init();
		Response response = QaBot.getInstance().response(
				StringUtils.StringAddSpace("�������"), "1");
		System.out.println(response.answer());
	}
}
