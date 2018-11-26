package org.summerb.utils;

import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.google.common.base.Throwables;

/**
 * Got it from:
 * http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
 * 
 * Utility for making deep copies (vs. clone()'s shallow copies) of objects.
 * Objects are first serialized and then deserialized. Error checking is fairly
 * minimal in this implementation. If an object is encountered that cannot be
 * serialized (or that references an object that cannot be serialized) an error
 * is printed to System.err and null is returned. Depending on your specific
 * application, it might make more sense to have copy(...) re-throw the
 * exception.
 */
public class DeepCopy {

	/**
	 * Returns a copy of the object, or null if the object cannot be serialized.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copy(T orig) {
		try {
			// Write the object out to a byte array
			FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(fbos);
			out.writeObject(orig);
			out.flush();
			out.close();

			// Retrieve an input stream from the byte array and read
			// a copy of the object back in.
			ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
			return (T) in.readObject();
		} catch (Throwable e) {
			throw new RuntimeException("Failed to make a deep copy of the original object", e);
		}
	}

	/**
	 * Returns a copy of the object, or throws exception if the object cannot be
	 * serialized.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copyOrPopagateExcIfAny(T orig) throws NotSerializableException {
		try {
			// Write the object out to a byte array
			FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(fbos);
			out.writeObject(orig);
			out.flush();
			out.close();

			// Retrieve an input stream from the byte array and read
			// a copy of the object back in.
			ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
			return (T) in.readObject();
		} catch (Throwable e) {
			Throwables.propagateIfPossible(e, NotSerializableException.class);
			throw new RuntimeException("Failed to make a deep copy of the original object", e);
		}
	}

}