/*
 * Copyleft (C) 2004 H閘io Perroni Filho xperroni@yahoo.com ICQ: 2490863 This
 * file is part of ChatterBean. ChatterBean is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. ChatterBean is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with ChatterBean (look at the
 * Documentos/ directory); if not, either write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA, or visit
 * (http://www.gnu.org/licenses/gpl.txt).
 */

package bitoflife.chatterbean;

import bitoflife.chatterbean.aiml.Category;
import bitoflife.chatterbean.text.Request;
import bitoflife.chatterbean.text.Response;
import bitoflife.chatterbean.text.Sentence;
import bitoflife.chatterbean.text.Transformations;

public class AliceBot {

	/*
	 * Attribute Section
	 */

	/** Context information for this bot current conversation. */
	private Context context;

	/** The Graphmaster maps user requests to AIML categories. */
	private Graphmaster graphmaster;

	/*
	 * Constructor Section
	 */

	/**
	 * Default constructor.
	 */
	public AliceBot() {
	}

	/**
	 * Creates a new AliceBot from a Context and a Graphmaster.
	 * 
	 * @param context
	 *            A Context.
	 * @param graphmaster
	 *            A Graphmaster.
	 */
	public AliceBot(Context context, Graphmaster graphmaster) {
		setContext(context);
		setGraphmaster(graphmaster);
	}

	/**
	 * Creates a new AliceBot from a Graphmaster.
	 * 
	 * @param graphmaster
	 *            Graphmaster object.
	 */
	public AliceBot(Graphmaster graphmaster) {
		setContext(new Context());
		setGraphmaster(graphmaster);
	}

	/*
	 * Method Section
	 */

	/**
	 * Returns this AliceBot's Context.
	 * 
	 * @return The Context associated to this AliceBot.
	 */
	public Context getContext() {
		return context;
	}

	public Graphmaster getGraphmaster() {
		return graphmaster;
	}

	/**
	 * Responds a request.
	 * 
	 * @param request
	 *            A Request.
	 * @return A response to the request.
	 */
	public Response respond(Request request, String userId) {
		// 用户发送聊天信息
		String original = request.getOriginal();
		// 判断如果为空就直接返回空结果
		if (original == null || "".equals(original.trim())) {
			return new Response("false");
		}
		// Sentence that = context.getThat();
		// Sentence topic = context.getTopic();
		// transformations().normalization(request);
		// context.appendRequest(request);

		Sentence that = context.getThat(userId);
		Sentence topic = context.getTopic(userId);
		// 进行分词 20140306 格式化enum节点数据
		// if (!StringUtils.isEmpty(request.getLexicon())) {
		// transformations().formatInputByCpSeg(request, request.getLexicon());
		// }
		transformations().normalization(request);
		context.appendRequest(request, userId);

		Response response = new Response();
		for (Sentence sentence : request.getSentences()) {
			respond(sentence, that, topic, response, userId);
		}
		// context.appendResponse(response);
		context.appendResponse(response, userId);

		return response;
	}

	/*
	 * Accessor Section
	 */

	private void respond(Sentence sentence, Sentence that, Sentence topic,
			Response response, String userId) {
		if (sentence.length() > 0) {
			Match match = new Match(this, sentence, that, topic, userId);
			// 新增20140306判断是否存在starValue并添加
			match.appendWildcard(sentence.getStarValue(), userId);
			Category category = graphmaster.match(match);
			System.out.println(category.toString() + "***" + that.toString()
					+ "********" + userId);
			response.append(category.process(match), category);
		}
	}

	/*
	 * Property Section
	 */

	public Response response(String input, String userId) {
		try {
			// input = PunctuationCache.getInstance().filterStringAll(input);
			input = input.replaceAll("\\...|\\.|\\!|\\?|\\;|\\,|\\:", "");
			Response response = respond(new Request(input, ""), userId);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Responds a request.
	 * 
	 * @param A
	 *            request string.
	 * @return A response to the request string.
	 */
	// public String respond(String input, String userId) {
	// try {
	// input = PunctuationCache.getInstance().filterStringAll(input);
	// Response response = respond(new Request(input), userId);
	// return response.trimOriginal();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return "false";
	// }
	public Response response(String input, String userId, String lexicon) {
		try {
			// input = PunctuationCache.getInstance().filterStringAll(input);
			input = input.replaceAll("\\...|\\.|\\!|\\?|\\;|\\,|\\:", "");
			Response response = respond(new Request(input, lexicon), userId);
			return response;
		} catch (Exception e) {

		}
		return null;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setGraphmaster(Graphmaster graphmaster) {
		this.graphmaster = graphmaster;
	}

	public Transformations transformations() {
		return context.getTransformations();
	}
}
