package me.snowleo.yaml;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;


@SupportedAnnotationTypes("me.snowleo.yaml.YamlClass")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class YamlPropertyProcessor extends AbstractProcessor
{
	private transient Filer filer;
	private transient Messager messager;
	private Elements eltUtils;

	public YamlPropertyProcessor()
	{
		super();
	}

	@Override
	public void init(final ProcessingEnvironment processingEnv)
	{
		super.init(processingEnv);
		filer = processingEnv.getFiler();
		messager = processingEnv.getMessager();
		eltUtils = processingEnv.getElementUtils();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> set, final RoundEnvironment roundEnv)
	{
		for (Element elem : roundEnv.getElementsAnnotatedWith(YamlClass.class))
		{
			if (elem.getKind() != ElementKind.CLASS)
			{
				messager.printMessage(Diagnostic.Kind.ERROR, "YamlClass has to be a class", elem);
				continue;
			}
			final TypeElement clazz = (TypeElement)elem;
			String superClassName = clazz.getSuperclass().toString();
			if (!superClassName.startsWith(clazz.getEnclosingElement().toString()))
			{
				superClassName = clazz.getEnclosingElement().toString() + "." + superClassName;
			}
			if (!superClassName.equalsIgnoreCase(clazz.getQualifiedName().toString() + "Yaml"))
			{
				messager.printMessage(Diagnostic.Kind.ERROR, "YamlClass must be extended by " + clazz.getQualifiedName().toString() + "Yaml", elem);
				continue;
			}
			final List<VariableElement> fields = new ArrayList<VariableElement>();
			for (VariableElement field : ElementFilter.fieldsIn(clazz.getEnclosedElements()))
			{
				if (!(field.getModifiers().contains(Modifier.STATIC))
					&& field.getModifiers().contains(Modifier.PROTECTED))
				{
					fields.add(field);
				}
				else if (field.getAnnotation(YamlComment.class) != null)
				{
					messager.printMessage(Diagnostic.Kind.ERROR, "YamlComment fields have to be nonstatic and protected.", field);
					continue;
				}
			}
			if (fields.isEmpty())
			{
				messager.printMessage(Diagnostic.Kind.ERROR, "YamlClass needs at least one protected field.", clazz);
				continue;
			}
			createImplclass(clazz, fields);
			createSuperclass(clazz, fields);
		}
		return true;
	}

	private void createSuperclass(final TypeElement clazz, final List<VariableElement> fields)
	{
		try
		{
			final JavaFileObject file = filer.createSourceFile(clazz.getQualifiedName() + "Yaml");
			messager.printMessage(Diagnostic.Kind.NOTE, "Creating " + file.toUri());

			final Writer writer = file.openWriter();
			//Add the content to the newly generated file.

			final PrintWriter pwriter = new PrintWriter(writer);
			try
			{
				pwriter.print("package ");
				pwriter.print(clazz.getEnclosingElement());
				pwriter.println(';');
				pwriter.println();
				pwriter.print("public class ");
				pwriter.print(clazz.getSimpleName());
				pwriter.print("Yaml extends BaseYaml<");
				pwriter.print(clazz.getSimpleName());
				pwriter.println("> {");
				
				pwriter.println("	@Override");
				pwriter.print("	protected Class<");
				pwriter.print(clazz.getSimpleName());
				pwriter.print("YamlImpl> getClazz() { return ");
				pwriter.print(clazz.getSimpleName());
				pwriter.println("YamlImpl.class; };");

				for (VariableElement var : fields)
				{
					final TypeMirror type = var.asType();
					final String name = capitalize(var.getSimpleName().toString());
					pwriter.print("	public ");
					pwriter.print(type.toString());
					if (type.toString().equalsIgnoreCase("boolean"))
					{
						pwriter.print(" is");
					}
					else
					{
						pwriter.print(" get");
					}
					pwriter.print(name);
					pwriter.println("() { throw new AssertionError(\"Cannot be called.\"); }");
					pwriter.print("	public void set");
					pwriter.print(name);
					pwriter.print("(final ");
					pwriter.print(type.toString());
					pwriter.print(' ');
					pwriter.print(name);
					pwriter.println(") { throw new AssertionError(\"Cannot be called.\"); }");
				}
				pwriter.println("}");
				pwriter.flush();
			}
			finally
			{
				pwriter.close();
			}
		}
		catch (IOException ex)
		{
			messager.printMessage(Diagnostic.Kind.ERROR, ex.toString());
		}
	}
	
	private void createImplclass(final TypeElement clazz, final List<VariableElement> fields)
	{
		try
		{
			final JavaFileObject file = filer.createSourceFile(clazz.getQualifiedName() + "YamlImpl");
			messager.printMessage(Diagnostic.Kind.NOTE, "Creating " + file.toUri());

			final Writer writer = file.openWriter();
			//Add the content to the newly generated file.

			final PrintWriter pwriter = new PrintWriter(writer);
			try
			{
				pwriter.print("package ");
				pwriter.print(clazz.getEnclosingElement());
				pwriter.println(';');
				pwriter.println();
				pwriter.print("public class ");
				pwriter.print(clazz.getSimpleName());
				pwriter.print("YamlImpl extends ");
				pwriter.print(clazz.getSimpleName());
				pwriter.println(" {");

				for (VariableElement var : fields)
				{
					final TypeMirror type = var.asType();
					final String name = capitalize(var.getSimpleName().toString());
					pwriter.print("	public ");
					pwriter.print(type.toString());
					if (type.toString().equalsIgnoreCase("boolean"))
					{
						pwriter.print(" is");
					}
					else
					{
						pwriter.print(" get");
					}
					pwriter.print(name);
					pwriter.print("() { return this.");
					pwriter.print(var.getSimpleName().toString());
					pwriter.println("; }");
					pwriter.print("	public void set");
					pwriter.print(name);
					pwriter.print("(final ");
					pwriter.print(type.toString());
					pwriter.print(' ');
					pwriter.print(name);
					pwriter.print(") { this.");
					pwriter.print(var.getSimpleName().toString());
					pwriter.print(" = ");
					pwriter.print(name);
					pwriter.println("; }");
				}
				pwriter.println("}");
				pwriter.flush();
			}
			finally
			{
				pwriter.close();
			}
		}
		catch (IOException ex)
		{
			messager.printMessage(Diagnostic.Kind.ERROR, ex.toString());
		}
	}

	private static String capitalize(String name)
	{
		char[] c = name.toCharArray();
		c[0] = Character.toUpperCase(c[0]);
		return new String(c);
	}
}
