package bitoflife.chatterbean.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import org.apache.log4j.Logger;

import bitoflife.chatterbean.AliceBot;
import bitoflife.chatterbean.Context;
import bitoflife.chatterbean.parser.AliceBotParser;
import bitoflife.chatterbean.util.Searcher;

/**
 * 问答机器人 Title. <br>
 * Description.
 * <p>
 * Copyright: Copyright (c) May 26, 2014 2:43:08 PM
 * <p>
 * Company: 北京宽连十方数字技术有限公司
 * <p>
 * Author: wangboa@c-platform.com
 * <p>
 * Version: 1.0
 * <p>
 */
public class QaBot extends Thread {
	private static ByteArrayOutputStream gossip;
	private static AliceBot instance;

	private static Logger log = Logger.getLogger(QaBot.class);

	public static AliceBot getInstance() {
		if (instance == null) {
			init();
		}
		return instance;
	}

	public static void init() {
		reStart();
	}

	public static AliceBot reStart() {
		try {
			gossip = new ByteArrayOutputStream();
			Searcher searcher = new Searcher();
			AliceBotParser parser = new AliceBotParser();
			instance = parser.parse(
					new FileInputStream("./config/context.xml"),
					new FileInputStream("./config/splitters.xml"),
					new FileInputStream("./config/substitutions.xml"), searcher
							.search("./aiml", "(qa_.*|app_.*)\\.aiml"));
			Context context = instance.getContext();
			context.outputStream(gossip);
		} catch (Exception e) {
			log.error("问答语机器人启动失败。。。", e);
		}
		return instance;
	}

	public QaBot() {

	}

	@SuppressWarnings("unused")
	private String gossip() {
		return gossip.toString();
	}

	@Override
	public void run() {
		reStart();
		log.info("重启成功。。。");
	}
}
