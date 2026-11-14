# ResenhaBoost 0.1.1

Mod client-side de otimização para **Minecraft 26.3 + Forge**.

## Controles

Pressione **F8** para ativar o modo desempenho. O preset reduz biome blend, nuvens, partículas, sombras de entidades, ambient occlusion, distância de entidades e distância de renderização (máximo de 8 chunks).

Pressione **F8** novamente para restaurar as opções anteriores.

## Compilar

Requer Java 25 e Gradle compatível:

```bash
gradle build --no-daemon
```

O JAR será gerado em `build/libs/`. O GitHub Actions compila automaticamente a cada push e publica o JAR como artefato da execução.

**Validação:** a compilação automatizada está em andamento. Ainda será necessário testar o mod dentro do Minecraft.