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

@Name("Definition of a Word")
@Description("Gets the definition of a Word.")
@Examples("load definition of \"doorway\" and save in {_doorway}")
@Since("1.1.0")
public class EffDefinition extends AsyncEffect {

    private Expression<String> word;
    private Variable<?> var;

    static {
        Skript.registerEffect(EffDefinition.class, "(get|load) [the] definition of %string% and (store|save) [it] in %-objects%");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {

        word = (Expression<String>) expressions[0];
        if (expressions[1] instanceof Variable<?>) {
            var = (Variable<?>) expressions[1];
        } else {
            Skript.error("You can only save definitions in variables!");
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
                        if ((line.contains("\"definitions\":[{\"definition\":\""))) {
                            String[] splits = line.split(Pattern.quote("\"definitions\":[{\"definition\":\""));
                            String[] moresplits = splits[1].split(Pattern.quote("\",\"synonyms"));
                            String[] definition = new String[]{moresplits[0]};

                            if (var != null) {
                                var.change(e, definition, Changer.ChangeMode.SET);
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