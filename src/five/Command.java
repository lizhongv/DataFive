//创建命令类
package five;

public class Command {// 用于客户端和服务器之间进行信息传递
	public static final String TELLNAME = "tellName";// tellName:用户名
	public static final String ADD = "add";// add:用户名:状态
	public static final String JOIN = "join";// join:对手
	public static final String REFUSE = "refuse";// refuse:对手
	public static final String AGREE = "agree";// agree:对手
	public static final String CHANGE = "change";// change:用户名/对手:playing
	public static final String GUESSCOLOR = "guessColor";// guessColor:white/black:用户名/对手名
	public static final String GO = "go";
	public static final String WIN = "win";
	public static final String TELLRESULT = "tellResult";// tellResult:wine/losses
	public static final String GIVEUP = "giveup";
	public static final String QUIT = "quit";
	public static final String DELETE = "delete";// delete:用户名

	public static final String REGISTER = "register";
	public static final String LOGIN = "login";// login命令:userName:passWord
	public static final String GAME = "game";
	public static final String MANUAL = "manual";

	// public static final String TALK = "talk";

}
