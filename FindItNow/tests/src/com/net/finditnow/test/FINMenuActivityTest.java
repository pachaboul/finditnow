package com.net.finditnow.test;

import com.net.finditnow.FINMenu;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.GridView;
import android.widget.ListAdapter;

public class FINMenuActivityTest extends ActivityInstrumentationTestCase2<FINMenu> {

	private FINMenu finmenu;
	private GridView gv;
	private ListAdapter gvAdapter;
	private int mPos;
	private long mSelection;

	public static final int ADAPTER_COUNT = 8;
	public static final int INITIAL_POSITION = 0;
	public static final int TEST_POSITION = 3;

	public FINMenuActivityTest() {
	    super("com.android.finditnow.FINMenu", FINMenu.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(false);

		finmenu = getActivity();

		gv = (GridView)finmenu.findViewById(com.net.finditnow.R.id.gridview);
		gvAdapter = gv.getAdapter();
	}

	public void testPreCondition1() {
		assertTrue(gv.getContext() != null);
	}
	
	public void testPreCondition2() {
		assertTrue(gvAdapter != null);
	}
	
	public void testPreCondition3() {
		assertEquals(gvAdapter.getCount(), ADAPTER_COUNT);
	}

	public void testMenuUI() {
		finmenu.runOnUiThread(
				new Runnable() {
					public void run() {
						gv.requestFocus();
						gv.setSelection(INITIAL_POSITION);
					}
				}
		);

		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		for (int i = 1; i < TEST_POSITION; i++) {
			this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		}

		mPos = gv.getSelectedItemPosition();
		mSelection = gv.getItemIdAtPosition(mPos);

		assertEquals(0, mSelection);
	}
}
