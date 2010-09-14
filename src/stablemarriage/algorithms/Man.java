package stablemarriage.algorithms;

public class Man
    extends StableMarriagePlayer {
  public Man(int nPreferencesSize) {
    super(nPreferencesSize);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param nPreferencesSize
   * @param sName
   */
  public Man(int nPreferencesSize, String sName) {
    super(/*nPreferencesSize, */sName);
    // TODO Auto-generated constructor stub
  }

  public StableMarriagePlayer PopFirstPreference() {
    try {
      return (StableMarriagePlayer) (m_lPreferencesList.removeFirst());
    }
    catch (Exception e) {
      //System.out.println(e.toString());
      return null;
    }
  }

}
