package forge.screens.quest;

import forge.FThreads;
import forge.model.FModel;
import forge.quest.QuestUtil;
import forge.screens.LaunchScreen;
import forge.screens.LoadingOverlay;
import forge.toolbox.FOptionPane;

public abstract class QuestLaunchScreen extends LaunchScreen {
    protected static final float PADDING = FOptionPane.PADDING;

    public QuestLaunchScreen() {
        super("", QuestMenu.getMenu());
    }

    @Override
    public final void onActivate() {
        update();
    }

    @Override
    protected void startMatch() {
        if (creatingMatch) { return; }
        creatingMatch = true; //ensure user doesn't create multiple matches by tapping multiple times

        FThreads.invokeInBackgroundThread(new Runnable() {
            @Override
            public void run() {
                if (QuestUtil.canStartGame()) {
                    FThreads.invokeInEdtLater(new Runnable() {
                        @Override
                        public void run() {
                            LoadingOverlay.show("Loading new game...", new Runnable() {
                                @Override
                                public void run() {
                                    QuestUtil.finishStartingGame();
                                    creatingMatch = false;
                                }
                            });
                        }
                    });
                    return;
                }
                creatingMatch = false;
            }
        });
    }

    @Override
    protected final boolean buildLaunchParams(LaunchParams launchParams) {
        return false; //this override isn't needed
    }

    public final void update() {
        QuestUtil.updateQuestView(QuestMenu.getMenu());
        setHeaderCaption(FModel.getQuest().getName() + " - " + getGameType() + "\n(" + FModel.getQuest().getRank() + ")");

        onUpdate();
    }

    protected abstract String getGameType();
    protected abstract void onUpdate();
}
