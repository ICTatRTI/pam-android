package org.rti.rcd.researchstack;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.ui.step.body.DateQuestionBody;
import org.researchstack.backbone.ui.step.body.DecimalQuestionBody;
import org.researchstack.backbone.ui.step.body.DurationQuestionBody;
import org.researchstack.backbone.ui.step.body.FormBody;
import org.researchstack.backbone.ui.step.body.IntegerQuestionBody;
import org.researchstack.backbone.ui.step.body.MultiChoiceQuestionBody;
import org.researchstack.backbone.ui.step.body.SingleChoiceQuestionBody;
import org.researchstack.backbone.ui.step.body.TextQuestionBody;


public class PamAnswerFormat extends AnswerFormat {

    private AnswerFormat.ChoiceAnswerStyle answerStyle;
    private Choice[] choices;



    /**
     * Creates an answer format with the specified answerStyle(single or multichoice) and collection
     * of choices.
     *
     * @param answerStyle either MultipleChoice or SingleChoice
     * @param choices     an array of {@link Choice} objects, all of the same type
     */
    public PamAnswerFormat(AnswerFormat.ChoiceAnswerStyle answerStyle, Choice... choices) {
        this.answerStyle = answerStyle;
        this.choices = choices.clone();
    }

    public enum Type implements QuestionType {
        Pam(PamStepBody.class),
        SingleChoice(SingleChoiceQuestionBody.class),
        MultipleChoice(MultiChoiceQuestionBody.class),
        Decimal(DecimalQuestionBody.class),
        Integer(IntegerQuestionBody.class),
        Boolean(SingleChoiceQuestionBody.class),
        Text(TextQuestionBody.class),
        TimeOfDay(DateQuestionBody.class),
        DateAndTime(DateQuestionBody.class),
        Date(DateQuestionBody.class),
        Duration(DurationQuestionBody.class),
        Form(FormBody.class);

        private Class<?> stepBodyClass;

        Type(Class<?> stepBodyClass)
        {
            this.stepBodyClass = stepBodyClass;
        }
        @Override
        public Class<?> getStepBodyClass() {
            return stepBodyClass;
        }
    }
    /**
     * Returns a multiple choice or single choice question type, which will decide which {@link
     * org.researchstack.backbone.ui.step.body.StepBody} to use to display this question.
     *
     * @return the question type for this answer format
     */
    @Override
    public QuestionType getQuestionType() {
        return Type.Pam;
    }

    /**
     * Returns a copy of the choice array
     *
     * @return a copy of the choices for this question
     */
    public Choice[] getChoices() {
        return choices.clone();
    }


}
