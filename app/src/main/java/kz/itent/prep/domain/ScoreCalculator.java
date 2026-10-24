package kz.itent.prep.domain;
import kz.itent.prep.data.Models.Question;
import java.util.List;
public final class ScoreCalculator {
 private ScoreCalculator() {}
 public static int score(List<Question> questions,int[] answers){int score=0; for(int i=0;i<questions.size();i++) if(answers!=null&&i<answers.length&&answers[i]==questions.get(i).correctIndex)score++; return score;}
 public static long remainingMillis(long deadline,long now){return Math.max(0,deadline-now);}
}
