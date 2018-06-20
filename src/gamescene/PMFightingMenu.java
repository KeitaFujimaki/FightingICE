package gamescene;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;

import enumerate.GameSceneName;
import informationcontainer.MenuItem;
import input.Keyboard;
import loader.ResourceLoader;
import manager.GraphicManager;
import manager.InputManager;
import setting.FlagSetting;
import setting.GameSetting;
import setting.LaunchSetting;
import struct.Key;

/**
 * 使用AI, 使用キャラクター, 繰り返し回数をセットするメニュー画面を扱うクラス．
 */
public class PMFightingMenu extends FightingMenu {

	/**
	 * 画面に表示する項目数．
	 */
	private final int NUMBER_OF_ITEM = 6;

	/**
	 * 表示する各項目のインデックス,名前,座標を格納している配列．
	 */
	private MenuItem[] menuItems;

	/**
	 * aiフォルダ内にある全AIの名前を格納したリスト．
	 */
	private ArrayList<String> allAiNames;

	/**
	 * 現在のカーソル位置．
	 */
	private int cursorPosition;

	/**
	 * 繰り返し回数(Repeat Count)の項目における現在の選択位置．
	 */
	private int numberIndex;

	/**
	 * PLAYERの項目における現在の選択位置．<br>
	 */
	private int playerIndex;

	/**
	 * CHARACTERの項目における現在の選択位置．<br>
	 * Index 0: P1; Index 1: P2
	 */
	private int[] characterIndexes;

	/** クラスコンストラクタ． */
	public PMFightingMenu() {
		// 以下4行の処理はgamesceneパッケージ内クラスのコンストラクタには必ず含める
		this.gameSceneName = GameSceneName.FIGHTING_MENU;
		this.isGameEndFlag = false;
		this.isTransitionFlag = false;
		this.nextGameScene = null;
		//////////////////////////////////////
	}

	@Override
	public void initialize() {
		InputManager.getInstance().setSceneName(GameSceneName.FIGHTING_MENU);

		this.menuItems = new MenuItem[] { new MenuItem("PLAY ", 50, 50, 0), new MenuItem("PLAYER1 : ", 75, 90, 1),
				new MenuItem("CHARACTER1 : ", 75, 170, 2),	new MenuItem("CHARACTER2 : ", 75, 210, 3), 
				new MenuItem("Repeat Count : ", 50, 260, 4), new MenuItem("RETURN ", 50, 310, 5) };

		this.playerIndex = 0;
		this.characterIndexes = new int[2];
		this.cursorPosition = 0;
		this.numberIndex = 0;

		this.allAiNames = ResourceLoader.getInstance().loadFileNames("./data/pmai", ".jar");
	}

	@Override
	public void update() {
		Key key = InputManager.getInstance().getKeyData().getKeys()[0];

		if (key.U) {
			if (this.cursorPosition == 0) {
				this.cursorPosition = this.menuItems[this.NUMBER_OF_ITEM - 1].getCursorPosition();
			} else {
				this.cursorPosition = this.menuItems[this.cursorPosition - 1].getCursorPosition();
			}
		}

		if (key.D) {
			if (this.cursorPosition == this.NUMBER_OF_ITEM - 1) {
				this.cursorPosition = this.menuItems[0].getCursorPosition();
			} else {
				this.cursorPosition = this.menuItems[this.cursorPosition + 1].getCursorPosition();
			}
		}

		switch (this.cursorPosition) {
		// PLAYの項目の位置のとき
		case 0:
			if (key.A) {
				// Launch情報をセット
				for (int i = 0; i < 2; i++) {
					LaunchSetting.pmaiName = this.allAiNames.get(this.playerIndex);
					LaunchSetting.characterNames[i] = GameSetting.CHARACTERS[this.characterIndexes[i]];

					LaunchSetting.deviceTypes[i] = InputManager.DEVICE_TYPE_PMAI;

				}

				LaunchSetting.repeatNumber = GameSetting.REPEAT_NUMBERS[this.numberIndex];
				if (LaunchSetting.repeatNumber > 1) {
					FlagSetting.automationFlag = true;
				}

				// Launcherの次の遷移先を登録
				Launcher launcher = new Launcher(GameSceneName.PLAY);
				this.setTransitionFlag(true);
				this.setNextGameScene(launcher);
			}
			break;

		// PLAYER1の位置のとき
		case 1:
			if (key.R) {
				if (this.playerIndex == this.allAiNames.size() - 1) {
					this.playerIndex = 0;
				} else {
					this.playerIndex++;
				}
			}
			if (key.L) {
				if (this.playerIndex == 0) {
					this.playerIndex = this.allAiNames.size() - 1;
				} else {
					this.playerIndex--;
				}
			}
			break;

		// CHARACTER1の位置のとき
		case 2:
			if (key.R) {
				if (this.characterIndexes[0] == GameSetting.CHARACTERS.length - 1) {
					this.characterIndexes[0] = 0;
				} else {
					this.characterIndexes[0]++;
				}
			}
			if (key.L) {
				if (this.characterIndexes[0] == 0) {
					this.characterIndexes[0] = GameSetting.CHARACTERS.length - 1;
				} else {
					this.characterIndexes[0]--;
				}
			}
			break;

		// CHARACTER2の位置のとき
		case 3:
			if (key.R) {
				if (this.characterIndexes[1] == GameSetting.CHARACTERS.length - 1) {
					this.characterIndexes[1] = 0;
				} else {
					this.characterIndexes[1]++;
				}
			}
			if (key.L) {
				if (this.characterIndexes[1] == 0) {
					this.characterIndexes[1] = GameSetting.CHARACTERS.length - 1;
				} else {
					this.characterIndexes[1]--;
				}
			}
			break;
		// Repeat Countの位置のとき
		case 4:
			if (key.R) {
				if (this.numberIndex == GameSetting.REPEAT_NUMBERS.length - 1) {
					this.numberIndex = 0;
				} else {
					this.numberIndex++;
				}
			}
			if (key.L) {
				if (this.numberIndex == 0) {
					this.numberIndex = GameSetting.REPEAT_NUMBERS.length - 1;
				} else {
					this.numberIndex--;
				}
			}
			break;

		// RETURNの位置のとき
		case 5:
			if (key.A) {
				HomeMenu homeMenu = new HomeMenu();
				this.setTransitionFlag(true);
				this.setNextGameScene(homeMenu);
			}
			break;

		default:
			break;
		}

		if (Keyboard.getKeyDown(GLFW_KEY_ESCAPE)) {
			HomeMenu homeMenu = new HomeMenu();
			this.setTransitionFlag(true);
			this.setNextGameScene(homeMenu);
		}
		// 画面の描画
		this.drawScreen();
	}

	/**
	 * 対戦の設定を行うメニュー画面を描画する．
	 */
	private void drawScreen() {
		GraphicManager.getInstance().drawString(this.menuItems[0].getString(), this.menuItems[0].getCoordinateX(),
				this.menuItems[0].getCoordinateY());
		GraphicManager.getInstance().drawString(
				this.menuItems[1].getString() + this.allAiNames.get(this.playerIndex),
				this.menuItems[1].getCoordinateX(), this.menuItems[1].getCoordinateY());
		GraphicManager.getInstance().drawString(
				this.menuItems[2].getString() + GameSetting.CHARACTERS[this.characterIndexes[0]],
				this.menuItems[2].getCoordinateX(), this.menuItems[2].getCoordinateY());
		GraphicManager.getInstance().drawString(
				this.menuItems[3].getString() + GameSetting.CHARACTERS[this.characterIndexes[1]],
				this.menuItems[3].getCoordinateX(), this.menuItems[3].getCoordinateY());
		GraphicManager.getInstance().drawString(
				this.menuItems[4].getString() + GameSetting.REPEAT_NUMBERS[this.numberIndex],
				this.menuItems[4].getCoordinateX(), this.menuItems[4].getCoordinateY());
		GraphicManager.getInstance().drawString(this.menuItems[5].getString(), this.menuItems[5].getCoordinateX(),
				this.menuItems[5].getCoordinateY());
		GraphicManager.getInstance().drawString("=>", this.menuItems[this.cursorPosition].getCoordinateX() - 30,
				this.menuItems[this.cursorPosition].getCoordinateY());
	}

	@Override
	public void close() {
		this.menuItems = null;
		this.allAiNames.clear();
		this.characterIndexes = null;
		this.playerIndex = 0;
	}

}