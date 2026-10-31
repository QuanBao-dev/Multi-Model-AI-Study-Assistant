package model;

/**
 * Anything that wants to hear when the QuerySession changes.
 */
public interface ModelObserver {
    void modelChanged();
}
