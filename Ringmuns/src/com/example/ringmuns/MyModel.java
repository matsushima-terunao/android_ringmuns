package com.example.ringmuns;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * モデル。
 * 
 * @author 2014/08 matsushima
 *
 */
public class MyModel {

	/** 頂点座標 */
	private static final float[][] vertices = {
		{
			-0.8f,-0.8f, 0.8f,  0.8f,-0.8f, 0.8f, -0.8f,0.8f, 0.8f,  0.8f,0.8f, 0.8f,
			 0.8f,-0.8f,-0.8f, -0.8f,-0.8f,-0.8f,  0.8f,0.8f,-0.8f, -0.8f,0.8f,-0.8f,
		}, {
			0,1.2f,0, -1,0,1, 1,0,1, 1,0,-1, -1,0,-1, 0,-1.2f,0,
		}, {
			-1.0f,1.2f,0, -1.0f,-1.2f,0, 1.0f,-1.2f,0, 1.0f,1.2f,0,
			-0.6f,0.7f, 0.5f, -0.6f,-0.7f, 0.5f, 0.6f,-0.7f, 0.5f, 0.6f,0.7f, 0.5f,
			-0.6f,0.7f,-0.5f, -0.6f,-0.7f,-0.5f, 0.6f,-0.7f,-0.5f, 0.6f,0.7f,-0.5f,
		}, {
			-0.6f,1.0f,-0.6f, -0.6f,1.0f,0.6f, 0.6f,1.0f,0.6f, 0.6f,1.0f,-0.6f,
			-1.0f,0.5f,-1.0f, -1.0f,0.5f,1.0f, 1.0f,0.5f,1.0f, 1.0f,0.5f,-1.0f,
			0.0f,-1.2f,0.0f,
		}, {
			-0.6f, 1.0f,-0.6f, -0.6f, 1.0f,0.6f, 0.6f, 1.0f,0.6f, 0.6f, 1.0f,-0.6f,
			-1.0f, 0.0f,-1.0f, -1.0f, 0.0f,1.0f, 1.0f, 0.0f,1.0f, 1.0f, 0.0f,-1.0f,
			-0.6f,-1.0f,-0.6f, -0.6f,-1.0f,0.6f, 0.6f,-1.0f,0.6f, 0.6f,-1.0f,-0.6f,
		}, { // KIND_FLUSH
			0,0.6f,0, -0.5f,0,0.5f, 0.5f,0,0.5f, 0.5f,0,-0.5f, -0.5f,0,-0.5f, 0,-0.6f,0,
		}, { // KIND_FLOOR
			0,0,0,
			3 * GameMain.WIDTH * (float)Math.sin(2 * Math.PI * 0.5 / 7), 0, 3 * GameMain.WIDTH * (float)Math.cos(2 * Math.PI * 0.5 / 7),
			3 * GameMain.WIDTH * (float)Math.sin(2 * Math.PI * 1.5 / 7), 0, 3 * GameMain.WIDTH * (float)Math.cos(2 * Math.PI * 1.5 / 7),
			3 * GameMain.WIDTH * (float)Math.sin(2 * Math.PI * 2.5 / 7), 0, 3 * GameMain.WIDTH * (float)Math.cos(2 * Math.PI * 2.5 / 7),
			3 * GameMain.WIDTH * (float)Math.sin(2 * Math.PI * 3.5 / 7), 0, 3 * GameMain.WIDTH * (float)Math.cos(2 * Math.PI * 3.5 / 7),
			3 * GameMain.WIDTH * (float)Math.sin(2 * Math.PI * 4.5 / 7), 0, 3 * GameMain.WIDTH * (float)Math.cos(2 * Math.PI * 4.5 / 7),
			3 * GameMain.WIDTH * (float)Math.sin(2 * Math.PI * 5.5 / 7), 0, 3 * GameMain.WIDTH * (float)Math.cos(2 * Math.PI * 5.5 / 7),
			3 * GameMain.WIDTH * (float)Math.sin(2 * Math.PI * 6.5 / 7), 0, 3 * GameMain.WIDTH * (float)Math.cos(2 * Math.PI * 6.5 / 7),
		}
	};
	/** 頂点インデックス */
	private static final int[][][] points = {
		{{0,1,2,3}, {4,5,6,7}, {5,0,7,2}, {1,4,3,6}, {2,3,7,6}, {5,4,0,1}},
		{{0,1,2}, {0,2,3}, {0,3,4}, {0,4,1}, {1,5,2}, {2,5,3}, {3,5,4}, {4,5,1}},
		{{0,4,3,7}, {1,5,0,4}, {2,6,1,5}, {3,7,2,6}, {4,5,7,6},  {3,11,0,8}, {0,8,1,9}, {1,9,2,10}, {2,10,3,11}, {9,8,10,11}},
		{{0,1,3,2},  {0,4,1,5}, {1,5,2,6}, {2,6,3,7}, {3,7,0,4},  {4,8,5}, {5,8,6}, {6,8,7}, {7,8,4}},
		{{0,1,3,2},  {0,4,1,5}, {1,5,2,6}, {2,6,3,7}, {3,7,0,4},  {4,8,5,9}, {5,9,6,10}, {6,10,7,11}, {7,11,4,8}},
		{{0,1,2}, {0,2,3}, {0,3,4}, {0,4,1}, {1,5,2}, {2,5,3}, {3,5,4}, {4,5,1}}, // KIND_FLUSH
		{{0,1,2}, {0,2,3}, {0,3,4}, {0,4,5}, {0,5,6}, {0,6,7}, {0,7,1}}, // KIND_FLOOR
	};
	/** 面の色 */
	private static final float[][][] colors = {
		{{0.5f,0,0,1}},
		{{0,0.5f,0,1}},
		{{0,0,0.5f,1}},
		{{0.5f,0,0.5f,1}},
		{{0,0.5f,0.5f,1}},
		{{1,1,0,1}}, // KIND_FLUSH
		{{0.5f,0,0,1}, {0.5f,0.5f,0,1}, {0,0.5f,0,1}, {0,0.5f,0.5f,1}, {0,0,0.5f,1}, {0.5f,0,0.5f,1}, {0.5f,0.5f,0.5f,1}}, // KIND_FLOOR
	};
	/** 頂点バッファ */
	private static FloatBuffer[] vertexBuffer = new FloatBuffer[vertices.length];
	/** 面法線ベクトル */
	private static float[][] normalVectors = new float[vertices.length][];

	// 種別
	public static final int KIND_DROP_CNT = 5;
	public static final int KIND_FLUSH = 5;
	public static final int KIND_FLOOR = 6;

	/** 種別 */
	public int kind;
	/** 消去済み */
	public boolean deleted;
	/** 座標 */
	public float x, y, z;
	/** 角度 */
	public float b;

	/**
	 * モデルを構築。
	 */
	{
		for (int k = 0; k < vertices.length; ++ k) {
			// 頂点数
			int vert_cnt = 0;
			for (int p = 0; p < points[k].length; ++ p) {
				vert_cnt += points[k][p].length;
			}
			// 頂点バッファ
			ByteBuffer buf = ByteBuffer.allocateDirect(vert_cnt * 3 * 4);
			buf.order(ByteOrder.nativeOrder());
			vertexBuffer[k] = buf.asFloatBuffer();
			// 面法線ベクトル
			normalVectors[k] = new float[points[k].length * 3];
			for (int p = 0; p < points[k].length; ++ p) {
				// 頂点バッファ
				for (int v = 0; v < points[k][p].length; ++ v) {
					vertexBuffer[k].put(vertices[k][points[k][p][v] * 3 + 0]);
					vertexBuffer[k].put(vertices[k][points[k][p][v] * 3 + 1]);
					vertexBuffer[k].put(vertices[k][points[k][p][v] * 3 + 2]);
				}
				// 面法線ベクトル
				float vx1 = vertices[k][points[k][p][1] * 3 + 0] - vertices[k][points[k][p][0] * 3 + 0];
				float vx2 = vertices[k][points[k][p][2] * 3 + 0] - vertices[k][points[k][p][0] * 3 + 0];
				float vy1 = vertices[k][points[k][p][1] * 3 + 1] - vertices[k][points[k][p][0] * 3 + 1];
				float vy2 = vertices[k][points[k][p][2] * 3 + 1] - vertices[k][points[k][p][0] * 3 + 1];
				float vz1 = vertices[k][points[k][p][1] * 3 + 2] - vertices[k][points[k][p][0] * 3 + 2];
				float vz2 = vertices[k][points[k][p][2] * 3 + 2] - vertices[k][points[k][p][0] * 3 + 2];
				float nx = vy1 * vz2 - vy2 * vz1;
				float ny = vz1 * vx2 - vz2 * vx1;
				float nz = vx1 * vy2 - vx2 * vy1;
				float nr = (float)Math.sqrt(nx * nx + ny * ny + nz * nz);
				normalVectors[k][p * 3 + 0] = nx / nr;
				normalVectors[k][p * 3 + 1] = ny / nr;
				normalVectors[k][p * 3 + 2] = nz / nr;
			}
			vertexBuffer[k].position(0);
		}
	}

	/**
	 * モデルを描画。
	 * 
	 * @param gl
	 */
	public void draw(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(x + MyRenderer.translateX, y + MyRenderer.translateY, z + MyRenderer.translateZ); // 移動
		gl.glRotatef(MyRenderer.rotateX, 1, 0, 0); // 回転: x軸
		gl.glRotatef(b + MyRenderer.rotateY, 0, 1, 0); // 回転: y軸
		gl.glRotatef(MyRenderer.rotateZ, 0, 0, 1); // 回転: z軸
		gl.glFrontFace(GL10.GL_CCW); // 全面: 反時計回り
		gl.glEnable(GL10.GL_CULL_FACE); // 片面を表示しない
		gl.glCullFace(GL10.GL_BACK); // 裏面を表示しない
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY); // 頂点配列を有効
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer[kind]); // xyz, float, padding なし, vertexBuffer
		if (MyRenderer.vertexNormal) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY); // 法線配列を有効
			gl.glNormalPointer(GL10.GL_FLOAT, 0, vertexBuffer[kind]); // xyz, float, padding なし, vertexBuffer
		} else {
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY); // 法線配列を無効
		}
		int point = 0;
		for (int p = 0; p < points[kind].length; ++ p) {
			if (p < colors[kind].length) {
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, colors[kind][p], 0);
			}
			if (MyRenderer.planeNormal) {
				gl.glNormal3f(normalVectors[kind][p * 3 + 0], normalVectors[kind][p * 3 + 1], normalVectors[kind][p * 3 + 2]); // 面法線ベクトル
			}
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, point, points[kind][p].length); // 0120 1231 // プリミティブを描画
			point += points[kind][p].length;
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY); // 頂点配列を無効
		gl.glDisable(GL10.GL_CULL_FACE); // 片面を表示しないを無効
		gl.glPopMatrix();
	}
}
