package me.jake.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import me.jake.utils.ConfigManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Name("Random words")
@Description("Returns one or more words in any of the following languages:\n\nSpanish, Chinese, Italian, German and English\n\nEnglish by default\nDue to api limitations you cannot get a specific amount of words with a non english language nor can you get a specific length of words with a non english language (amount is also limited by this unless you manually get more than one, this might be a future feature).")
@Examples("")
@Since("1.2.0")
public class EffRandomWords extends AsyncEffect {

    private Expression<Number> amount;
    private Expression<Number> length;
    private Variable<?> var;

    String language;

    static {
        Skript.registerEffect(EffRandomWords.class, "(get|load) [a[n]|%-number%] random [(:spanish|:chinese|:italian|:german|english)] word[s] [with (length|size) %-number%] (and (store|save) [(it|them)] in|to) %-objects%");
    }
    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        if (parseResult.hasTag("spanish")) {
            language = "es";
        } else if (parseResult.hasTag("chinese")) {
            language = "zh";
        } else if (parseResult.hasTag("italian")) {
            language = "it";
        } else if (parseResult.hasTag("german")) {
            language = "de";
        }
        if (length != null) {
            if (language == null) {
                length = (Expression<Number>) expressions[1];
            }
        } else {
            if (amount != null) {
                if (language == null) {
                    amount = (Expression<Number>) expressions[0];
                }
            }
        }
        if (expressions[2] instanceof Variable<?>) {
            var = (Variable<?>) expressions[2];
        } else {
            Skript.error("You can only save random words in variables!");
        }
        try {
            Boolean randomWordWarning = ConfigManager.getBoolean(ConfigManager.getStoredConfig(), ("randomWordWarning"));
            if (Boolean.TRUE.equals(randomWordWarning)) {
                Skript.warning("Due to API limitations this effect is limited, meaning you must choose between language, length and amount. If you'd like to get more than 1 then just use this effect as much as you need but unfortunately you cannot get a specified length for languages other than English. This warning will be present during every reload that includes this effect, to disable it, set the config option \"randomWordWarning\" to false.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    @Override
    protected void execute(@NotNull Event e) {
        if (var != null) {
            String webTag;
            if (language != null) {
                webTag = "?lang="+language;
            } else if (length != null) {
                Number l = length.getSingle(e);
                webTag = "?length="+l;
            } else if (amount != null) {
                Number a = amount.getSingle(e);
                webTag = "?number="+a;
            } else {
                webTag = "";
            }
            URL url;
            try {
                url = new URL("https://random-word-api.herokuapp.com/word" + webTag);
            } catch (MalformedURLException Event) {
                throw new RuntimeException(Event);
            }
            URLConnection con;
            try {
                con = url.openConnection();
            } catch (IOException Event) {
                throw new RuntimeException(Event);
            }
            InputStream is;
            try {
                is = con.getInputStream();
            } catch (IOException Event) {
                throw new RuntimeException(Event);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                if ((line = br.readLine()) != null) {
                    if (amount == null) {
                        String[] split = line.split("\"");
                        String[] word = new String[]{split[1]};
                        var.change(e, word, Changer.ChangeMode.SET);
                    } else {
                        String replace = line.replaceAll("[\\[\\]\"]","");
                        String[] split = replace.split(",");
                        var.change(e, split, Changer.ChangeMode.SET);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }
    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return null;
    }
}