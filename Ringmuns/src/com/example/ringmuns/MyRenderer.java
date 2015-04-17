package com.example.ringmuns;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

/**
 * レンダラー。
 * 
 * @author 2014/08 matsushima
 *
 */
public class MyRenderer implements Renderer {

	private GameMain controller = new GameMain();

	/** ビューポート */
	private int width, height;

	// option
	/** 視野 */
	public static float perspectiveFovy = 45, perspectiveNearZ = 1, perspectiveFarZ = 100;
	/** 視点 */
	public static float lookAtEyeX = 0, lookAtEyeY = 0, lookAtEyeZ = 0;
	/** 視点 */
	public static float lookAtCenterX = 0, lookAtCenterY = -0.35f, lookAtCenterZ = -1;
	/** 視点 */
	public static float lookAtUpperX = 0, lookAtUpperY = 1, lookAtUpperZ = 0;
	/** 移動 */
	public static float translateX = 0, translateY = 0, translateZ = -50;
	/** 回転 */
	public static float rotateX = 0, rotateY = 0, rotateZ = 0;
	/** 照明の位置 */
	public static boolean isLightPosition = true;
	/** 照明の位置 */
	public static float[] lightPosition = {-20.0f, 20.0f, 100.0f, 1.0f};
	/** 環境光 */
	public static boolean isLightAmbient = true;
	/** 環境光 */
	public static float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
	/** 拡散光 */
	public static boolean isLightDiffuse = true;
	/** 拡散光 */
	public static float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
	/** 鏡面光 */
	public static boolean isLightSpecular = true;
	/** 鏡面光 */
	public static float[] lightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
	/** 光無効 */
	public static float[] lightZero = {0, 0, 0, 0};
	/** 面法線 */
	public static boolean planeNormal = false;
	/** 頂点法線 */
	public static boolean vertexNormal = true;
	/** option が変更された */
	public static volatile boolean optionChanged = false;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glEnable(GL10.GL_DEPTH_TEST); // デプスバッファを有効
		gl.glDepthFunc(GL10.GL_LEQUAL); // デプスバッファ比較: 以下
		if (isLightPosition) {
			gl.glLightfv(GL10.GL_LIGHT0,  GL10.GL_POSITION, lightPosition, 0); // 照明の位置
			gl.glLightfv(GL10.GL_LIGHT0,  GL10.GL_AMBIENT, isLightAmbient ? lightAmbient : lightZero, 0); // 環境光
			gl.glLightfv(GL10.GL_LIGHT0,  GL10.GL_DIFFUSE, isLightDiffuse ? lightDiffuse : lightZero, 0); // 拡散光
			gl.glLightfv(GL10.GL_LIGHT0,  GL10.GL_SPECULAR, isLightSpecular ? lightSpecular : lightZero, 0); // 鏡面光
			gl.glEnable(GL10.GL_LIGHTING); // ライティング有効
			gl.glEnable(GL10.GL_LIGHT0); // ライティング0有効
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.width = width;
		this.height = height;
		gl.glViewport(0, 0, width, height); // ビューポートの設定
		gl.glMatrixMode(GL10.GL_PROJECTION); // 行列の選択: 射影
		gl.glLoadIdentity(); // 単位行列に置き換え
		GLU.gluPerspective(gl, perspectiveFovy, (float)width / height, perspectiveNearZ, perspectiveFarZ); // 視野の設定
		gl.glMatrixMode(GL10.GL_MODELVIEW); // 行列の選択: モデルビュー
		gl.glLoadIdentity(); // 単位行列に置き換え
		GLU.gluLookAt(gl, lookAtEyeX, lookAtEyeY, lookAtEyeZ, lookAtCenterX, lookAtCenterY, lookAtCenterZ, lookAtUpperX, lookAtUpperY, lookAtUpperZ); // 視点の設定
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// メイン処理。
		controller.proc();
		// 描画
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); // バッファクリア: カラーバッファ, デプスバッファ
		if (optionChanged) {
			optionChanged = false;
			gl.glMatrixMode(GL10.GL_PROJECTION); // 行列の選択: 射影
			gl.glLoadIdentity(); // 単位行列に置き換え
			GLU.gluPerspective(gl, perspectiveFovy, (float)width / height, perspectiveNearZ, perspectiveFarZ); // 視野の設定
			gl.glMatrixMode(GL10.GL_MODELVIEW); // 行列の選択: モデルビュー
			gl.glLoadIdentity(); // 単位行列に置き換え
			GLU.gluLookAt(gl, lookAtEyeX, lookAtEyeY, lookAtEyeZ, lookAtCenterX, lookAtCenterY, lookAtCenterZ, lookAtUpperX, lookAtUpperY, lookAtUpperZ); // 視点の設定
			if (isLightPosition) {
				gl.glLightfv(GL10.GL_LIGHT0,  GL10.GL_POSITION, lightPosition, 0); // 照明の位置
				gl.glLightfv(GL10.GL_LIGHT0,  GL10.GL_AMBIENT, isLightAmbient ? lightAmbient : lightZero, 0); // 環境光
				gl.glLightfv(GL10.GL_LIGHT0,  GL10.GL_DIFFUSE, isLightDiffuse ? lightDiffuse : lightZero, 0); // 拡散光
				gl.glLightfv(GL10.GL_LIGHT0,  GL10.GL_SPECULAR, isLightSpecular ? lightSpecular : lightZero, 0); // 鏡面光
				gl.glEnable(GL10.GL_LIGHTING); // ライティング有効
				gl.glEnable(GL10.GL_LIGHT0); // ライティング0有効
			} else {
				gl.glDisable(GL10.GL_LIGHTING); // ライティング無効
				gl.glDisable(GL10.GL_LIGHT0); // ライティング0無効
			}
		}
		// 描画。
		controller.draw(gl);
	}
}
