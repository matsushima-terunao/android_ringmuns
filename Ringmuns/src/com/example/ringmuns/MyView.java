package com.example.ringmuns;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * �r���[�B
 * 
 * @author 2014/08 matsushima
 *
 */
public class MyView extends GLSurfaceView {

	public MyRenderer renderer;

	public MyView(Context context) {
		super(context);
		renderer = new MyRenderer();
		setRenderer(renderer); // �����_���[�̐ݒ�
	}
}
