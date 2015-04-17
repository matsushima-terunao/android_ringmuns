package com.example.ringmuns;

import javax.microedition.khronos.opengles.GL10;

import android.os.Handler;
import android.widget.TextView;

/**
 * �Q�[�����C���B
 * 
 * @author 2014/08 matsushima
 *
 */
public class GameMain {

	/** �^�b�`���Ԃ̂������l */
	private static final long TOUCH_TIME = 300;
	/** �^�b�`�ړ��ʂ̂������l */
	private static final float TOUCH_DISTANCE = 1;
	/** models �̃J������ */
	public static final int COLS = 7;
	/** models �̍s�� */
	public static final int ROWS = 15;
	/** model �̕� */
	public static final float WIDTH = 2.5f;

	/** �Q�[����� */
	enum State {
		/** game over */
		GAME_OVER,
		/** ������ */
		DROPPING,
		/** ���n��Œ�O */
		DROPPED,
		/** ���蒆 */
		JUDGING,
		/** ����� */
		JUDGED,
	}

	/** �Q�[����� */
	private State state = State.GAME_OVER; // game over
	/** �Q�[����Ԋ���� */
	private long stateTime = 0;
	/** ����� */
	private int judgeCount;
	/** ���_ */
	private int score;
	/** �X�s�[�h */
	private int speed;

	/** �L�[ */
	public static boolean keyLeft, keyRight, keyDown, keyFlip, keyStart;
	/** �}�E�X */
	public static boolean mouseLeft, mouseLeftPrev;
	/** �}�E�X */
	public static long mouseTime, mouseLeftDownTime;
	/** �}�E�X */
	public static int mouseXLeftDown, mouseYLeftDown, mouseXPrev, mouseYPrev, mouseX, mouseY;

	/** �s�� */
	private MyModel[][] models = new MyModel[COLS][ROWS];
	/** �h���b�v */
	private MyModel[] dropModels = new MyModel[3];
	/** ���̃h���b�v */
	private MyModel[] nextModels = new MyModel[3];
	/** �� */
	private MyModel floor;
	/** �h���b�v�J�E���g */
	private int dropCount;
	/** �h���b�v���W */
	private int dropX;
	/** �h���b�v���W */
	private float dropY;
	Handler handler = new Handler();

	public GameMain() {
		floor = new MyModel();
		floor.kind = MyModel.KIND_FLOOR;
		floor.y = -ROWS * WIDTH + WIDTH / 4;
	}

	/**
	 * ���C�������B
	 */
	public void proc() {
		try {
			long curTime = System.currentTimeMillis();
			// �ړ�����
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
					// �_�E��
					mouseLeftDownTime = mouseTime;
					mouseXLeftDown = mouseXPrev = mouseX;
					mouseYLeftDown = mouseYPrev = mouseY;
				}
			} else {//if (!mouseLeft) {
				if (mouseLeftPrev) {
					// �A�b�v
					if (mouseTime - mouseLeftDownTime < TOUCH_TIME
							&& Math.abs(mouseX - mouseXLeftDown) < TOUCH_DISTANCE
							&& Math.abs(mouseY - mouseYLeftDown) < TOUCH_DISTANCE) {
						// �^�b�`���Ԃ̂������l���� and �ړ��Ȃ� -> �^�b�v
						flip = true;
					}
				}
			}
			// mouse move
			if ((mouseLeft && mouseLeftPrev) || (!mouseLeft && mouseLeftPrev)) {
				if (Math.abs(mouseX - mouseXLeftDown) >= Math.abs(mouseY - mouseYLeftDown)) {
					int x = (mouseX - mouseXLeftDown) / 3;
					// �^�b�`���Ԃ̂������l���� ? �t���b�N : �X���C�v
					if (mouseTime - mouseLeftDownTime < TOUCH_TIME) {
						x = (0 == x ? 0 : x > 0 ? 1 : -1);
					}
					dx = -(x - (mouseXPrev - mouseXLeftDown) / 3);
					mouseX = mouseXLeftDown + x * 3;
				} else {
					int y = mouseY - mouseYLeftDown;
					// �^�b�`���Ԃ̂������l���� ? �t���b�N : �X���C�v
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
			// �ړ�
			float dropYPrev = dropY;
			for (int i = 0; i < dy; ++ i) {
				if (State.DROPPING == state) { // ������
					dropYPrev = dropY;
					dropY = (curTime - stateTime) / speed;
					if (dropY > dropYPrev + 1) {
						dropY = dropYPrev + 1;
					}
					if ((dropY + 2 >= ROWS - 1) || (null != models[dropX][(int)dropY + 2 + 1])) {
						// ���n
						dropY = (float)(int)dropY;
						state = State.DROPPED; // ���n��Œ�O
						stateTime = curTime;
					} else {
						stateTime -= speed;
					}
				} else if (State.DROPPED == state) { // ���n��Œ�O
					// ���n��1�b�o��
					stateTime = curTime - 1000;
					break;
				}
			}
			for (int i = 0; i < Math.abs(dx); ++ i) {
				if ((State.DROPPING == state) || (State.DROPPED == state)) { // ������ // ���n��Œ�O
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

			// �Q�[����ԕʏ���
			switch (state) {
			case GAME_OVER: // game over
				if (keyStart) {
					state = State.DROPPING; // ������
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
			case DROPPING: // ������
				// ���n����
				dropY = (curTime - stateTime) / speed;
				if (dropY > dropYPrev + 1) {
					dropY = dropYPrev + 1;
				}
				if ((dropY + 2 >= ROWS - 1) || (null != models[dropX][(int)dropY + 2 + 1])) {
					// ���n
					dropY = (float)(int)dropY;
					state = State.DROPPED; // ���n��Œ�O
					stateTime = curTime;
				}
				break;
			case DROPPED: // ���n��Œ�O
				if (stateTime + 1000 > curTime) {
					if ((dropY + 2 >= ROWS - 1) || (null != models[dropX][(int)dropY + 2 + 1])) {
						// ���n��1�b����
					} else {
						// ���n��ė���
						state = State.DROPPING; // ������
						stateTime = curTime - (long)(dropY * speed);
					}
				} else {
					// ���n��1�b�o��
					System.out.println("d4:" + dropY);
					for (int i = 0; i < 3; ++ i) {
						models[dropX][(int)dropY + i] = dropModels[i];
						dropModels[i] = null;
					}
					state = State.JUDGING; // ���蒆
					stateTime = curTime - 500;
					judgeCount = 1;
				}
				break;
			case JUDGING: // ���蒆
				if (stateTime + 500 > curTime) {
					break;
				}
				// ��������
				int deleteCnt = 0;
				if ((null != models[dropX][(int)dropY]) && (MyModel.KIND_FLUSH == models[dropX][(int)dropY].kind)) {
					// drop flush
					if (dropY + 2 + 1 >= ROWS) {
						// �n��
						deleteCnt = 100;
					} else {
						// �����Ɠ�����ނ�����
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
					final int[] dirs = {1,0, 0,1, 1,1, -1,1}; // �E, ��, �E��, ����
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
				// ������������ ? flush : game over or new drop
				if (deleteCnt > 0) {
					// flush
					for (int x = 0; x < COLS; ++ x) {
						for (int y = 0; y < ROWS; ++ y) {
							if ((null != models[x][y]) && (models[x][y].deleted)) {
								models[x][y].kind = MyModel.KIND_FLUSH;
							}
						}
					}
					state = State.JUDGED; // �����
					stateTime = curTime;
				} else {
					// game over ����
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
					state = State.DROPPING; // ������
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
			case JUDGED: // �����
				if (stateTime + 500 > curTime) {
					break;
				}
				// �����ꂽ���̂��l�߂�
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
				state = State.JUDGING; // ���蒆
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
	 * �`��B
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
