package org.daisy.dotify.formatter.impl.search;

/**
 * <p>Provides basic volume data for identifiers.</p>
 * 
 * <p>Identifiers are used to mark data in the content so that it is possible
 * to refer to the place in the content where the identifier occurs after the
 * document has been processed. This class lists the volume related properties
 * of identifiers that we need elsewhere in the code.</p>
 * 
 * @author Paul Rambags
 */
public final class VolumeData {
	private final int volumeNumber;
	private final boolean atStartOfVolumeContents;
	
	public VolumeData(int volumeNumber, boolean atStartOfVolumeContents) {
		this.volumeNumber = volumeNumber;
		this.atStartOfVolumeContents = atStartOfVolumeContents;
	}
	
	public int getVolumeNumber() {
		return volumeNumber;
	}
	
	public boolean isAtStartOfVolumeContents() {
		return atStartOfVolumeContents;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + volumeNumber;
		result = prime * result + (atStartOfVolumeContents ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VolumeData other = (VolumeData) obj;
		if (volumeNumber != other.volumeNumber) {
			return false;
		}
		if (atStartOfVolumeContents != other.atStartOfVolumeContents) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "VolumeData [volumeNumber=" + volumeNumber + ", isAtStartOfVolumeContents=" + atStartOfVolumeContents + "]";
	}

}
