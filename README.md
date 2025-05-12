## Explicación del proyecto

Se llevaron a cabo las etapas para la elaboracion de un compilador.
Se implementara un parser recursivo - Descendente basado en el pseudocódigo visto en clase

### Componentes del Compilador
- scanner: Se recibe un codigo de alto nivel para poder detectar todos los tokens definidos

- Parser: Mediante los tokens anteriormente generados se implementa el uso de un parser recursivo que mediante producciones: se sigue una ruta de codigo en el que se validan que los tokens esten pasando correctamente por los estatutos

- Tabla de simbolos: se realiza un analisis semantico para generar la tabla de simbolos del programa, donde se encuentran las variables declaradas, y se le da logica al programa, donde se validan las condiciones, los bloques de codigo de las condiciones y los valores de las variables declaradas, si estan declaradas o no.

- Codigo intermedio: se realiza una traduccion a un lenguaje de bajo nivel despues de haber realizado el analisis, lexico, sintactico y semantico, para poder llevar a cabo la correcta traduccion hacia este lenguaje a ensamblador, donde en ella vienen OPcodes, operando1,operando2, ya que se siguio el modo de codigo de dos direcciones

- Codigo Objeto: se realizo una traduccion a lenguaje maquina despues de la traduccion de lenguaje de bajo nivel, en el que se traduce todos los OPCodes que se identificaron anteriormente para darles el correcto y debido valor en lenguaje maquina, ceros y unos; para ello se separaron en dos segmentos, segmento de datos y segmento de codigo, donde cada uno tiene una direccion de memoria y su debido valor.

## 
Si el parser o el scanner encuentra errores debera desplegar el error y despues parar la compilación (Tambien puede continuar buscando mas errores)



### Gramatica

```bash
P → D S EOF 
D → ID (int | string | dou ) ɛ ; D 
D → ɛ 
S → IF E {S} 
S → IF E {S} ELSE {S} 
S → ID = OPER 
S → Print OPER 
E → ID | D == ID | ID !== ID | ID > ID | ID >= ID | ID < ID | ID <= ID 
OPER → SUMA | RESTA | MULM | DIVM | ID 
SUMA → NUM + NUM | NUM + FRACC | FRACC + NUM | FRACC + FRACC  
RESTA → NUM – NUM | NUM – FRACC | FRACC – NUM | FRACC – FRACC 
MULM → NUM * NUM | NUM * FRACC | FRACC * NUM | FRACC * FRACC 
DIVD → NUM / NUM | NUM / FRACC | FRACC / NUM | FRACC / FRACC 
ID → LETRA (LETRA|DIGITO)* 
NUM → DIGITOP (DIGITOP)* | DIGITO (DIGITO)* 
DIGITO → 0 | 1 | 2 | 3 | 5 | 6 | 7 | 8 | 9 
DIGITOP → 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 
FRACC → DIGITO(DIGITO)*.DIGITO(DIGITO)* 
CADENA → LETRA(LETRA)* 
LETRA → 
A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s
 |t|u|v|w|x|y|z 
```

### primer código (error)

```bash
    x1 int ; print t4 +
    Error (se esperaba un id)
```

### segundo código (error)

```bash
    apuntes83 int ; persona string ; while persona do print print
    Error (se esperaba una expresion o un id)
```

### Tercer código (sin error)

```bash
    edad int ; altura dou ; IF edad == altura {
    altura = 12 + 12
    } ELSE {
    edad = 12 * 2
    }
```

## Autor

- [@Peña Lopez Miguel Angel](https://github.com/KingSplatt)