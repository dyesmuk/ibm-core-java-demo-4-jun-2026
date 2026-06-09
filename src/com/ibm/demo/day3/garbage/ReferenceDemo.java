package com.ibm.demo.day3.garbage;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class ReferenceDemo {

	public static void main(String[] args) throws InterruptedException {

		// ─── 1. Strong Reference ───────────────────────────────────────────
		String name = new String("Hello");
		System.out.println("Strong ref: " + name);
		name = null; // now eligible for GC
		System.gc();
		System.out.println("Strong ref set to null → object may be collected\n");

		// ─── 2. Soft Reference ────────────────────────────────────────────
		SoftReference<byte[]> cache = new SoftReference<>(new byte[1024]);

		byte[] data = cache.get(); // returns object if still alive
		if (data != null) {
			System.out.println("Soft ref — Cache hit: " + data.length + " bytes");
		} else {
			System.out.println("Soft ref — Cache miss: GC cleared it (low memory)");
		}
		System.out.println();

		// ─── 3. Weak Reference ────────────────────────────────────────────
		String data2 = new String("temporary");
		WeakReference<String> weak = new WeakReference<>(data2);

		System.out.println("Weak ref — Before GC: " + weak.get());

		data2 = null; // remove strong reference
		System.gc();
		Thread.sleep(100); // give GC a moment to run

		System.out.println("Weak ref — After  GC: " + weak.get() + "\n");

		// ─── 4. Phantom Reference ─────────────────────────────────────────
		// PhantomReference requires a ReferenceQueue.
		// get() ALWAYS returns null — the object is already finalized.
		// We detect collection by polling the queue.

		ReferenceQueue<Object> queue = new ReferenceQueue<>();

		Object resource = new Object();
		PhantomReference<Object> phantom = new PhantomReference<>(resource, queue);

		System.out.println("Phantom ref — get() before GC: " + phantom.get()); // always null

		resource = null; // remove strong reference
		System.gc();
		Thread.sleep(100); // give GC time to enqueue the phantom ref

		// Poll the queue to detect that the object has been collected
		if (queue.poll() != null) {
			System.out.println("Phantom ref — object collected → safe to run cleanup");
		} else {
			System.out.println("Phantom ref — object not yet collected");
		}
	}
}
