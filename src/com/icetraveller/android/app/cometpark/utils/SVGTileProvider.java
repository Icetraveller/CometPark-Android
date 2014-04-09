package com.icetraveller.android.app.cometpark.utils;


import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import static com.icetraveller.android.app.cometpark.utils.LogUtils.*;

public class SVGTileProvider implements TileProvider {
	private static final String TAG = makeLogTag(SVGTileProvider.class);

	private static final int POOL_MAX_SIZE = 5;
	private static final int BASE_TILE_SIZE = 256;

	private final TileGeneratorPool mPool;

	private final Matrix mBaseMatrix;

	private final int mScale;
	private final int mDimension;

	private byte[] mSvgFile;

	public SVGTileProvider(File file, float dpi) throws IOException {
		mScale = Math.round(dpi + .3f); // Make it look nice on N7 (1.3 dpi)
		mDimension = BASE_TILE_SIZE * mScale;

        mPool = new TileGeneratorPool(POOL_MAX_SIZE);

        mSvgFile = readFile(file);
		RectF limits = SVGParser.getSVGFromInputStream(new ByteArrayInputStream(mSvgFile)).getLimits();

        mBaseMatrix = new Matrix();
        mBaseMatrix.setPolyToPoly(
                new float[]{
                		limits.width(), 0,
                        0, limits.height(),
                        limits.width(), limits.height()
                }, 0,
                new float[]{
                		59.20057102222222f, 103.1282771843786f,
                		59.200258133333335f, 103.1287977144785f,
                		59.200571733333334f, 103.1288011055422f
                }, 0, 3);
    }

	@Override
	public Tile getTile(int x, int y, int zoom) {
		TileGenerator tileGenerator = mPool.get();
		byte[] tileData = tileGenerator.getTileImageData(x, y, zoom);
		mPool.restore(tileGenerator);
		return new Tile(mDimension, mDimension, tileData);
	}

	private class TileGeneratorPool {
		private final ConcurrentLinkedQueue<TileGenerator> mPool = new ConcurrentLinkedQueue<TileGenerator>();
		private final int mMaxSize;

		private TileGeneratorPool(int maxSize) {
			mMaxSize = maxSize;
		}

		public TileGenerator get() {
			TileGenerator i = mPool.poll();
			if (i == null) {
				return new TileGenerator();
			}
			return i;
		}

		public void restore(TileGenerator tileGenerator) {
			if (mPool.size() < mMaxSize && mPool.offer(tileGenerator)) {
				return;
			}
			// pool is too big or returning to pool failed, so just try to clean
			// up.
			tileGenerator.cleanUp();
		}
	}

	public class TileGenerator {
		private Bitmap mBitmap;
		private SVG mSvg;
		private ByteArrayOutputStream mStream;

		public TileGenerator() {
			mBitmap = Bitmap.createBitmap(mDimension, mDimension, Bitmap.Config.ARGB_8888);
			mStream = new ByteArrayOutputStream(mDimension * mDimension * 4);
            mSvg = SVGParser.getSVGFromInputStream(new ByteArrayInputStream(mSvgFile));
		}

		public byte[] getTileImageData(int x, int y, int zoom) {
			mStream.reset();

			Matrix matrix = new Matrix(mBaseMatrix);
			float scale = (float) (Math.pow(2, zoom) * mScale);
			matrix.postScale(scale, scale);
			matrix.postTranslate(-x * mDimension, -y * mDimension);

			mBitmap.eraseColor(Color.TRANSPARENT);
			Canvas c = new Canvas(mBitmap);
			c.setMatrix(matrix);
			mSvg.getPicture().draw(c);

			BufferedOutputStream stream = new BufferedOutputStream(mStream);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
			try {
				stream.close();
			} catch (IOException e) {
				Log.e(TAG, "Error while closing tile byte stream.");
				e.printStackTrace();
			}
			return mStream.toByteArray();
		}

		/**
		 * Attempt to free memory and remove references.
		 */
		public void cleanUp() {
			mBitmap.recycle();
			mBitmap = null;
			mSvg = null;
			try {
				mStream.close();
			} catch (IOException e) {
				// ignore
			}
			mStream = null;
		}
	}

    private static byte[] readFile(File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            return baos.toByteArray();
        } finally {
            in.close();
        }
    }
}
