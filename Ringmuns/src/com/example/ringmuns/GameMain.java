package com.example.ringmuns;

import javax.microedition.khronos.opengles.GL10;

import android.os.Handler;
import android.widget.TextView;

/**
 * ゲームメイン。
 * 
 * @author 2014/08 matsushima
 *
 */
public class GameMain {

	/** タッチ時間のしきい値 */
	private static final long TOUCH_TIME = 300;
	/** タッチ移動量のしきい値 */
	private static final float TOUCH_DISTANCE = 1;
	/** models のカラム数 */
	public static final int COLS = 7;
	/** models の行数 */
	public static final int ROWS = 15;
	/** model の幅 */
	public static final float WIDTH = 2.5f;

	/** ゲーム状態 */
	enum State {
		/** game over */
		GAME_OVER,
		/** 落下中 */
		DROPPING,
		/** 着地後固定前 */
		DROPPED,
		/** 判定中 */
		JUDGING,
		/** 判定後 */
		JUDGED,
	}

	/** ゲーム状態 */
	private State state = State.GAME_OVER; // game over
	/** ゲーム状態基準時刻 */
	private long stateTime = 0;
	/** 判定回数 */
	private int judgeCount;
	/** 得点 */
	private int score;
	/** スピード */
	private int speed;

	/** キー */
	public static boolean keyLeft, keyRight, keyDown, keyFlip, keyStart;
	/** マウス */
	public static boolean mouseLeft, mouseLeftPrev;
	/** マウス */
	public static long mouseTime, mouseLeftDownTime;
	/** マウス */
	public static int mouseXLeftDown, mouseYLeftDown, mouseXPrev, mouseYPrev, mouseX, mouseY;

	/** 行列 */
	private MyModel[][] models = new MyModel[COLS][ROWS];
	/** ドロップ */
	private MyModel[] dropModels = new MyModel[3];
	/** 次のドロップ */
	private MyModel[] nextModels = new MyModel[3];
	/** 床 */
	private MyModel floor;
	/** ドロップカウント */
	private int dropCount;
	/** ドロップ座標 */
	private int dropX;
	/** ドロップ座標 */
	private float dropY;
	Handler handler = new Handler();

	public GameMain() {
		floor = new MyModel();
		floor.kind = MyModel.KIND_FLOOR;
		floor.y = -ROWS * WIDTH + WIDTH / 4;
	}

	/**
	 * メイン処理。
	 */
	public void proc() {
		try {
			long curTime = System.currentTimeMillis();
			// 移動処理
			int dx = 0, dy = 0;
			boolean flip = false;
			// key
			if (keyLeft) {
				keyLeft = false;
				dx = -1;
			}
			if (keyRight) {
				keyRight = false;
				dx = 1;
			}
			if (keyDown) {
				keyDown = false;
				dy = 1;
			}
			if (keyFlip) {
				keyFlip = false;
				flip = true;
			}
			// mouse up down
			if (mouseLeft) {
				if (!mouseLeftPrev) {
					// ダウン
					mouseLeftDownTime = mouseTime;
					mouseXLeftDown = mouseXPrev = mouseX;
					mouseYLeftDown = mouseYPrev = mouseY;
				}
			} else {//if (!mouseLeft) {
				if (mouseLeftPrev) {
					// アップ
					if (mouseTime - mouseLeftDownTime < TOUCH_TIME
							&& Math.abs(mouseX - mouseXLeftDown) < TOUCH_DISTANCE
							&& Math.abs(mouseY - mouseYLeftDown) < TOUCH_DISTANCE) {
						// タッチ時間のしきい値未満 and 移動なし -> タップ
						flip = true;
					}
				}
			}
			// mouse move
			if ((mouseLeft && mouseLeftPrev) || (!mouseLeft && mouseLeftPrev)) {
				if (Math.abs(mouseX - mouseXLeftDown) >= Math.abs(mouseY - mouseYLeftDown)) {
					int x = (mouseX - mouseXLeftDown) / 3;
					// タッチ時間のしきい値未満 ? フリック : スワイプ
					if (mouseTime - mouseLeftDownTime < TOUCH_TIME) {
						x = (0 == x ? 0 : x > 0 ? 1 : -1);
					}
					dx = -(x - (mouseXPrev - mouseXLeftDown) / 3);
					mouseX = mouseXLeftDown + x * 3;
				} else {
					int y = mouseY - mouseYLeftDown;
					// タッチ時間のしきい値未満 ? フリック : スワイプ
					if (mouseTime - mouseLeftDownTime < TOUCH_TIME) {
						y = (0 == y ? 0 : y > 0 ? 1 : -1);
					}
					dy = y - (mouseYPrev - mouseYLeftDown);
					mouseY = mouseYLeftDown + y;
				}
			}
			mouseLeftPrev = mouseLeft;
			mouseXPrev = mouseX;
			mouseYPrev = mouseY;
			// 移動
			float dropYPrev = dropY;
			for (int i = 0; i < dy; ++ i) {
				if (State.DROPPING == state) { // 落下中
					dropYPrev = dropY;
					dropY = (curTime - stateTime) / speed;
					if (dropY > dropYPrev + 1) {
						dropY = dropYPrev + 1;
					}
					if ((dropY + 2 >= ROWS - 1) || (null != models[dropX][(int)dropY + 2 + 1])) {
						// 着地
						dropY = (float)(int)dropY;
						state = State.DROPPED; // 着地後固定前
						stateTime = curTime;
					} else {
						stateTime -= speed;
					}
				} else if (State.DROPPED == state) { // 着地後固定前
					// 着地後1秒経過
					stateTime = curTime - 1000;
					break;
				}
			}
			for (int i = 0; i < Math.abs(dx); ++ i) {
				if ((State.DROPPING == state) || (State.DROPPED == state)) { // 落下中 // 着地後固定前
					if (null != models[(dropX + COLS + (dx >= 0 ? 1 : -1)) % COLS][(int)dropY + 2]) {
						break;
					}
				}
				dropX = (dropX + COLS + (dx >= 0 ? 1 : -1)) % COLS;
			}
			if (flip) {
				MyModel model = dropModels[2];
				dropModels[2] = dropModels[1];
				dropModels[1] = dropModels[0];
				dropModels[0] = model;
			}

			// ゲーム状態別処理
			switch (state) {
			case GAME_OVER: // game over
				if (keyStart) {
					state = State.DROPPING; // 落下中
					stateTime = curTime;
					mouseLeft = mouseLeftPrev = false;
					score = 0;
					speed = 1000;
					dropCount = 1;
					dropX = 0;
					dropY = 0;
					models = new MyModel[COLS][ROWS];
					for (int i = 0; i < 3; ++ i) {
						dropModels[i] = new MyModel();
						dropModels[i].kind = (int)(Math.random() * MyModel.KIND_DROP_CNT);
						dropModels[i].b = i * 50;
						nextModels[i] = new MyModel();
						nextModels[i].kind = (int)(Math.random() * MyModel.KIND_DROP_CNT);
					}
				}
				break;
			case DROPPING: // 落下中
				// 着地判定
				dropY = (curTime - stateTime) / speed;
				if (dropY > dropYPrev + 1) {
					dropY = dropYPrev + 1;
				}
				if ((dropY + 2 >= ROWS - 1) || (null != models[dropX][(int)dropY + 2 + 1])) {
					// 着地
					dropY = (float)(int)dropY;
					state = State.DROPPED; // 着地後固定前
					stateTime = curTime;
				}
				break;
			case DROPPED: // 着地後固定前
				if (stateTime + 1000 > curTime) {
					if ((dropY + 2 >= ROWS - 1) || (null != models[dropX][(int)dropY + 2 + 1])) {
						// 着地後1秒未満
					} else {
						// 着地後再離陸
						state = State.DROPPING; // 落下中
						stateTime = curTime - (long)(dropY * speed);
					}
				} else {
					// 着地後1秒経過
					System.out.println("d4:" + dropY);
					for (int i = 0; i < 3; ++ i) {
						models[dropX][(int)dropY + i] = dropModels[i];
						dropModels[i] = null;
					}
					state = State.JUDGING; // 判定中
					stateTime = curTime - 500;
					judgeCount = 1;
				}
				break;
			case JUDGING: // 判定中
				if (stateTime + 500 > curTime) {
					break;
				}
				// 消去判定
				int deleteCnt = 0;
				if ((null != models[dropX][(int)dropY]) && (MyModel.KIND_FLUSH == models[dropX][(int)dropY].kind)) {
					// drop flush
					if (dropY + 2 + 1 >= ROWS) {
						// 地面
						deleteCnt = 100;
					} else {
						// 直下と同じ種類を消去
						int kind = models[dropX][(int)dropY + 2 + 1].kind;
						for (int x = 0; x < COLS; ++ x) {
							for (int y = 0; y < ROWS; ++ y) {
								if ((null != models[x][y]) && (kind == models[x][y].kind)) {
									models[x][y].deleted = true;
									++ deleteCnt;
								}
							}
						}
					}
				} else {
					final int[] dirs = {1,0, 0,1, 1,1, -1,1}; // 右, 下, 右下, 左下
					for (int d = 0; d < dirs.length; d += 2) {
						for (int x = 0; x < COLS; ++ x) {
							for (int y = 0; y < ROWS; ++ y) {
								int x1 = (x + COLS + dirs[d] * 1) % COLS;
								int x2 = (x + COLS + dirs[d] * 2) % COLS;
								int y1 = y + dirs[d + 1] * 1;
								int y2 = y + dirs[d + 1] * 2;
								if (y2 < ROWS
										&& null != models[x][y]
										&& null != models[x1][y1]
										&& null != models[x2][y2]
										&& models[x][y].kind == models[x1][y1].kind
										&& models[x][y].kind == models[x2][y2].kind) {
									deleteCnt += 3;
									models[x][y].deleted = true;
									models[x1][y1].deleted = true;
									models[x2][y2].deleted = true;
								}
							}
						}
					}
				}
				// 判定後消去あり ? flush : game over or new drop
				if (deleteCnt > 0) {
					// flush
					for (int x = 0; x < COLS; ++ x) {
						for (int y = 0; y < ROWS; ++ y) {
							if ((null != models[x][y]) && (models[x][y].deleted)) {
								models[x][y].kind = MyModel.KIND_FLUSH;
							}
						}
					}
					state = State.JUDGED; // 判定後
					stateTime = curTime;
				} else {
					// game over 判定
					for (int x = 0; x < COLS; ++ x) {
						if (null != models[x][2]) {
							state = State.GAME_OVER; // game over
							return;
						}
					}
					// new drop
					++ dropCount;
					boolean flush = (0 == dropCount % 100);
					for (int i = 0; i < 3; ++ i) {
						dropModels[i] = nextModels[i];
						dropModels[i].b = i * 50;
						nextModels[i] = new MyModel();
						nextModels[i].kind = (flush ? MyModel.KIND_FLUSH : (int)(Math.random() * MyModel.KIND_DROP_CNT));
					}
					state = State.DROPPING; // 落下中
					stateTime = curTime;
					mouseLeft = mouseLeftPrev = false;
					dropY = 0;
				}
				score += deleteCnt * 100 * judgeCount;
				speed = Math.max(100, 1000 - score / 100);
				final String textScore = "" + score;// + "," + speed;
				handler.post(new Runnable() {
					@Override
					public void run() {
						((TextView)MainActivity.activity.findViewById(R.id.textView1)).setText(textScore);
					}
				});
				break;
			case JUDGED: // 判定後
				if (stateTime + 500 > curTime) {
					break;
				}
				// 消されたものを詰める
				for (int x = 0; x < COLS; ++ x) {
					for (int y = ROWS - 1; y >= 0; ) {
						if (null == models[x][y]) {
							break;
						} else if (MyModel.KIND_FLUSH != models[x][y].kind) {
							-- y;
						} else {
							for (int i = y; i >= 1; -- i) {
								models[x][i] = models[x][i - 1];
							}
							models[x][0] = null;
						}
					}
				}
				state = State.JUDGING; // 判定中
				stateTime += 500;
				++ judgeCount;
				break;
			}
			keyLeft = keyRight = keyDown = keyFlip = keyStart = false;
		} catch (RuntimeException e) {
			System.out.println(e.toString());
			e.printStackTrace(System.out);
			throw e;
		}
	}

	private long drawTime = System.currentTimeMillis();

	/**
	 * 描画。
	 * 
	 * @param gl
	 */
	public void draw(GL10 gl) {
		floor.b = -360f * dropX / COLS;
		floor.draw(gl);
		for (int x = 0; x < COLS; ++ x) {
			float tx = (float)Math.sin(2 * Math.PI * (x - dropX) / COLS) * WIDTH * 2;
			float tz = (float)Math.cos(2 * Math.PI * (x - dropX) / COLS) * WIDTH * 2;
			for (int y = 0; y < ROWS; ++ y) {
				if (null != models[x][y]) {
					models[x][y].x = tx;
					models[x][y].y = -y * WIDTH;
					models[x][y].z = tz;
					models[x][y].b += floor.b;
					models[x][y].draw(gl);
					models[x][y].b -= floor.b;
				}
			}
		}
		long bTime = System.currentTimeMillis();
		float vb = (bTime - drawTime) * 360f / 2000f;
		drawTime = bTime;
		for (int i = 0; i < 3; ++ i) {
			if (null != dropModels[i]) {
				dropModels[i].x = 0;
				dropModels[i].y = -(dropY + i) * WIDTH;
				dropModels[i].z = WIDTH * 2;
				dropModels[i].b += vb;
				dropModels[i].b += floor.b;
				dropModels[i].draw(gl);
				dropModels[i].b -= floor.b;
			}
		}
		for (int i = 0; i < 3; ++ i) {
			if (null != nextModels[i]) {
				nextModels[i].x = WIDTH * 4;
				nextModels[i].y = -i * WIDTH;
				nextModels[i].z = 0;
				nextModels[i].b = 0;
				nextModels[i].draw(gl);
			}
		}
	}
}
