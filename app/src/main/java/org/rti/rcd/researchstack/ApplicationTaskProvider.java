package org.rti.rcd.researchstack;
import android.content.Context;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.skin.task.ConsentTask;
import org.researchstack.skin.task.SignInTask;
import org.researchstack.skin.task.SignUpTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApplicationTaskProvider extends org.researchstack.skin.TaskProvider
{
    private HashMap<String, Task> map = new HashMap<>();

    public ApplicationTaskProvider(Context context)
    {
        put(TASK_ID_CONSENT, ConsentTask.create(context, TASK_ID_CONSENT));
        put(TASK_ID_SIGN_IN, new SignInTask(context));
        put(TASK_ID_SIGN_UP, new SignUpTask(context));
    }

    @Override
    public Task get(String taskId)
    {
        return map.get(taskId);
    }

    @Override
    public void put(String id, Task task)
    {
        map.put(id, task);
    }

    private Task createInitialTask(Context context)
    {
        List<Step> steps = new ArrayList<>();
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Intro step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        InstructionStep step = new InstructionStep("intro",
                "About You",
                "We'd like to ask you a few questions to better understand potential robot risks\n\nThese questions should take less than 5 minutes");
        step.setStepTitle(R.string.task_inital_toolbar_title);

        // Add to Task
        steps.add(step);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Basic Info Form step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        FormStep basicInfoForm = new FormStep("basicInfo", "About You", "");
        basicInfoForm.setStepTitle(R.string.task_inital_toolbar_title);


        // Gender
        AnswerFormat genderFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>("Female", 0),
                new Choice<>("Male", 1),
                new Choice<>("Other", 2));
        QuestionStep genderStep = new QuestionStep("gender", "Gender", genderFormat);

        // Zip Code
        IntegerAnswerFormat zipCodeFormat = new IntegerAnswerFormat(0, 99999);
        QuestionStep zipCodeStep = new QuestionStep("zipCode",
                "What is your zip code?",
                zipCodeFormat);

        // Set items on FormStep
        basicInfoForm.setOptional(true);
        basicInfoForm.setFormSteps(genderStep, zipCodeStep);

        // Add to Task
        steps.add(basicInfoForm);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Eye and Hair color form step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        FormStep hairEyesForm = new FormStep("hairEyesInfo", "Natural Hair and Eye Color", "");
        hairEyesForm.setStepTitle(R.string.task_inital_toolbar_title);

        // Hair Color
        AnswerFormat hairColorFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>("Red Hair", "redHair"),
                new Choice<>("Blond Hair", "blondeHair"),
                new Choice<>("Brown Hair", "brownHair"),
                new Choice<>("Black Hair", "blackHair"));
        QuestionStep hairColorStep = new QuestionStep("hairColor", "Hair Color", hairColorFormat);

        AnswerFormat eyeColorFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>("Blue Eyes", "blueEyes"),
                new Choice<>("Green Eyes", "greenEyes"),
                new Choice<>("Brown Eyes", "brownEyes"));
        QuestionStep eyeColorStep = new QuestionStep("eyeColor", "Eye Color", eyeColorFormat);

        // Set items on FormStep
        hairEyesForm.setOptional(true);
        hairEyesForm.setFormSteps(hairColorStep, eyeColorStep);

        // Add to Task
        steps.add(hairEyesForm);

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Profession Step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*



        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Thank You Step
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        InstructionStep thankYouStep = new InstructionStep("thankYou",
                "Thank You!",
                "Your participation in this study is helping us to better understand risks of becoming a robot\n\nYour task now is to take robot surveys each month. You don't have to get them all, but the more the better!\n\nHappy robot-ing!");
        thankYouStep.setStepTitle(R.string.task_inital_toolbar_title);
        // Add to Task
        steps.add(thankYouStep);

        return new OrderedTask(TASK_ID_INITIAL, steps);
    }
}
