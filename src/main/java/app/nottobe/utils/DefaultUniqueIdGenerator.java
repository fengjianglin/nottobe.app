package app.nottobe.utils;

import org.springframework.stereotype.Component;

@Component
public final class DefaultUniqueIdGenerator implements UniqueIdGenerator {

	private final NumericGenerator numericGenerator;

	private final RandomStringGenerator randomStringGenerator;

	private final String suffix;

	public DefaultUniqueIdGenerator() {
		this(null);
	}

	public DefaultUniqueIdGenerator(final int maxLength) {
		this(maxLength, null);
	}

	public DefaultUniqueIdGenerator(final String suffix) {
		this.numericGenerator = new DefaultLongNumericGenerator(1);
		this.randomStringGenerator = new DefaultRandomStringGenerator();

		if (suffix != null) {
			this.suffix = "-" + suffix;
		} else {
			this.suffix = null;
		}
	}

	public DefaultUniqueIdGenerator(final int maxLength, final String suffix) {
		this.numericGenerator = new DefaultLongNumericGenerator(1);
		this.randomStringGenerator = new DefaultRandomStringGenerator(maxLength);

		if (suffix != null) {
			this.suffix = "-" + suffix;
		} else {
			this.suffix = null;
		}
	}

	public String getNewId(final String prefix) {
		final String number = this.numericGenerator.getNextNumberAsString();
		final StringBuilder buffer = new StringBuilder(
				prefix.length() + 2 + (this.suffix != null ? this.suffix.length() : 0)
						+ this.randomStringGenerator.getMaxLength() + number.length());

		buffer.append(prefix);
		buffer.append("-");
		buffer.append(number);
		buffer.append("-");
		buffer.append(this.randomStringGenerator.getNewString());

		if (this.suffix != null) {
			buffer.append(this.suffix);
		}

		return buffer.toString();
	}
}
