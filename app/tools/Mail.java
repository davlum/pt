package tools;

import akka.actor.ActorSystem;
import play.Configuration;
import play.Logger;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Mail {

    /**
     * 1 second delay on sending emails
     */
    private static final int DELAY = 1;

    /**
     * Envelop to prepare.
     */
    public static class Envelop {
        private String subject;
        private String message;
        private List<String> toEmails;

        public Envelop(String subject, String message, String email) {
            this.message = message;
            this.subject = subject;
            this.toEmails = new ArrayList<>();
            this.toEmails.add(email);
        }
    }

    /**
     * Send a email, using Akka to offload it to an actor.
     *
     * @param envelop envelop to send
     */
    public static void sendMail(Mail.Envelop envelop, MailerClient mailerClient, ActorSystem actorSystem) {
        EnvelopJob envelopJob = new EnvelopJob(envelop, mailerClient);
        final FiniteDuration delay = Duration.create(DELAY, TimeUnit.SECONDS);
        actorSystem.scheduler().scheduleOnce(delay, envelopJob, actorSystem.dispatcher());
    }

    private static class EnvelopJob implements Runnable {
        Mail.Envelop envelop;
        MailerClient mailerClient;

        EnvelopJob(Mail.Envelop envelop, MailerClient mailerClient) {
            this.envelop = envelop;
            this.mailerClient = mailerClient;
        }

        public void run() {
            Email email = new Email();

            final Configuration root = Configuration.root();
            final String mailFrom = "xtab.comp5541@gmail.com";
            email.setFrom(mailFrom);
            email.setSubject(envelop.subject);
            for (String toEmail : envelop.toEmails) {
                email.addTo(toEmail);
                Logger.debug("Mail.sendMail: Mail will be sent to " + toEmail);
            }

            email.setBodyHtml(envelop.message);

            Logger.info(mailerClient.toString());

            mailerClient.send(email);

            Logger.debug("Mail sent - SMTP:" + root.getString("play.mailer.host")
                    + ":" + root.getString("play.mailer.port")
                    + " SSL:" + root.getString("play.mailer.ssl")
                    + " user:" + root.getString("play.mailer.user")
                    + " password:" + root.getString("play.mailer.password"));
        }
    }

}