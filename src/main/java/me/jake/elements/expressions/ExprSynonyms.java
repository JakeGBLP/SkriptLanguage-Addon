package me.jake.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import  java.io.BufferedReader;
import  java.io.IOException;
import  java.io.InputStream;
import  java.io.InputStreamReader;
import  java.net.MalformedURLException;
import  java.net.URL;
import  java.net.URLConnection;
import  java.util.regex.Pattern;

@Name("Synonyms of a Word")
@Description("Returns the Synonyms of a Word." +
        "Can't be set." +
        "When using 1.0.0 it is not suggested to repeatedly use this expression due to performance issues.")
@Examples({"broadcast synonyms of \"maybe\"",
        "set {_AllYes::*} to synonyms of \"yes\""})
@Since("1.0.0")
public class ExprSynonyms extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprSynonyms.class, String.class, ExpressionType.COMBINED, "[the] synonym[s] of %string%");
    }
    private Expression<String> string;

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        string = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "Synonyms of %string% expression: " + string.toString(event, debug);
    }

    @Override
    protected String @NotNull [] get(@NotNull Event event) {
        String s = string.getSingle(event);
        if (s != null) {
            if (s.matches("^[ A-Za-z]+$")) {
                URL url;
                try {
                    url = new URL("https://api.dictionaryapi.dev/api/v2/entries/en/" + s);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                URLConnection con;
                try {
                    con = url.openConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                InputStream is;
                try {
                    is = con.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    if ((line = br.readLine()) != null) {
                        if ((line.contains("\"synonyms\":[\""))) {
                            String[] splits = line.split(Pattern.quote("\"synonyms\":[\""));
                            String[] moresplits = splits[1].split(Pattern.quote("]"));
                            String noquotes = moresplits[0].replaceAll("\"", "");
                            return noquotes.split(",");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return new String[0];
    }
}