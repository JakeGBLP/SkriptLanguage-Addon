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
import java.util.regex.Pattern;

@Name("Antonyms of a Word")
@Description("Returns the antonyms of a Word.")
@Examples("get the antonyms of \"yes\" and save them in {_notyes::*}")
@Since("1.1.0")
public class EffAntonyms extends AsyncEffect {

    private Expression<String> word;
    private Variable<?> var;

    static {
        Skript.registerEffect(EffAntonyms.class, "(get|load) [the] antonym[s] of %string% and (store|save) [(it|them)] in %-objects%");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {

        word = (Expression<String>) expressions[0];
        if (expressions[1] instanceof Variable<?>) {
            var = (Variable<?>) expressions[1];
        } else {
            Skript.error("You can only save antonyms in variables!");
        }

        return true;
    }

    @Override
    protected void execute(@NotNull Event e) {
        String string = this.word.getSingle(e);
        if (string != null) {
            if (string.matches("^[A-Za-z]+$")) {
                URL url;
                try {
                    url = new URL("https://api.dictionaryapi.dev/api/v2/entries/en/" + string);
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
                        if ((line.contains("\"antonyms\":[\""))) {
                            String[] splits = line.split(Pattern.quote("\"antonyms\":[\""));
                            String[] moresplits = splits[1].split(Pattern.quote("]"));
                            String noquotes = moresplits[0].replaceAll("\"", "");
                            String[] antonyms = noquotes.split(",");
                            if (var != null) {
                                var.change(e, antonyms, Changer.ChangeMode.SET);
                            }
                        }
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return null;
    }
}