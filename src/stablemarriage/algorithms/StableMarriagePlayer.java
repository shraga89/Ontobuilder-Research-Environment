package stablemarriage.algorithms;

import java.util.*;

public class StableMarriagePlayer {
  public StableMarriagePlayer(int nPreferencesSize) {
    m_bMarried = false;
    m_uPartner = null;
    m_sName = "";
    m_lPreferencesList = new LinkedList();
    m_nPreferencesSize = nPreferencesSize;
    for (int i = 0; i < m_nPreferencesSize; ++i) {
      m_lPreferencesList.add(i, null);
    }
  }

  public StableMarriagePlayer(String sName) {
    m_bMarried = false;
    m_uPartner = null;
    m_sName = sName;
    m_lPreferencesList = new LinkedList();
    /*m_nPreferencesSize = nPreferencesSize;
    for (int i = 0; i < m_nPreferencesSize; ++i) {
      m_lPreferencesList.add(i, null);
    }*/

  }

  public void SetName(String sName) {
    m_sName = sName;
  }

  public String GetName() {
    if(m_sName == null){
      return "";
    }
    return m_sName;
  }

  public boolean AddRankedPartner(StableMarriagePlayer rankedPartner, int rank) {
    if ( /*(rank > m_nPreferencesSize) || */(rankedPartner == null)) {
      return false;
    }

    if ( (!m_lPreferencesList.contains(rankedPartner))/* && (m_lPreferencesList.get(rank) == null)*/) {
      try {
        m_lPreferencesList.add(rank, rankedPartner);
      }
      catch (Exception e) {
        System.out.println(e.toString());
        return false;
      }
    }
    else {
      return false;
    }
    return true;
  }

  public boolean equals(StableMarriagePlayer object) {
    if (m_sName.equals(object.GetName())) {
      return true;
    }
    return false;
  }

  public StableMarriagePlayer PopFirstPreference() {
    return null;
  }

  public boolean MorePrefer(StableMarriagePlayer candidate) {
    return false;
  }

  public void SetPartner(StableMarriagePlayer newPartner) {
    m_uPartner = newPartner;
  }

  public StableMarriagePlayer GetPartner() {
    return m_uPartner;
  }

  public boolean Islegal() {
    /*if (m_lPreferencesList.size() != m_nPreferencesSize) {
      return false;
    }
    for (int i = 0; i < m_nPreferencesSize; ++i) {
      if (m_lPreferencesList.get(i) == null) {
        return false;
      }
    }*/
    return true;
  }

  public boolean IsMarried() {
    return this.m_bMarried;
  }

  public void SetMarried(boolean bMarried) {
    m_bMarried = bMarried;
  }

  public String toString() {
    StringBuffer str= new StringBuffer("                                                                  ");
    str.replace(0,m_sName.length()-1,m_sName);

    if (m_uPartner != null) {
      str.append(m_uPartner.GetName());
    }
    else {
      str.append("could not be matched");
    }
    return  str.toString();
  }

  String m_sName;
  boolean m_bMarried;
  StableMarriagePlayer m_uPartner;
  LinkedList m_lPreferencesList;
  int m_nPreferencesSize;

}
