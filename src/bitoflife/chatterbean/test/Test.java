package bitoflife.chatterbean.test;

import bitoflife.chatterbean.text.Response;
import bitoflife.chatterbean.util.StringUtils;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 第二个参数是用户id
		QaBot.init();
		Response response = QaBot.getInstance().response(
				StringUtils.StringAddSpace("想念祖国"), "1");
		System.out.println(response.answer());
	}
}
